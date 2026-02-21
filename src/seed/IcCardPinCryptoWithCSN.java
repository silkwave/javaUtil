package seed;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * 금융결제원 현금IC카드 PIN 암호화/복호화 (카드 난수 + 카드 일련번호 포함)
 */
public class IcCardPinCryptoWithCSN {

    private static final int BLOCK_SIZE = 16;

    private final byte[] masterKey;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * IcCardPinCryptoWithCSN 생성자.
     *
     * @param hexKey 16진수 문자열 형태의 16바이트 마스터 키.
     */
    public IcCardPinCryptoWithCSN(String hexKey) {
        // 마스터 키는 반드시 16바이트(32 Hex)여야 함
        if (hexKey == null || hexKey.length() != 32)
            throw new IllegalArgumentException("Master Key는 16바이트(32 HEX)여야 합니다.");
        this.masterKey = hexToBytes(hexKey);
    }

    /**
     * PIN, 카드 난수, 카드 일련번호(CSN)를 결합하여 암호화하고, 최종 전송 데이터를 생성합니다.
     * 이 메서드는 암호화의 전체 흐름을 담당합니다.
     *
     * @param pin        4~6자리 PIN
     * @param cardRandom IC카드에서 생성한 16바이트 난수
     * @param cardSerial 카드 일련번호, ASCII 문자열
     * @return IV + 카드난수 + 암호문 HEX
     */
    public String encryptPin(String pin, byte[] cardRandom, String cardSerial) {
        // 1. 입력값 유효성 검증
        validatePin(pin);
        validateCardRandom(cardRandom);
        validateCardSerial(cardSerial);

        printBox("IC카드 PIN 암호화 시작");
        printHex("A. 마스터 키", masterKey, BLOCK_SIZE);

        // 2. 터미널 난수(IV) 생성
        byte[] terminalRandom = generateRandomBytes(BLOCK_SIZE);
        printHex("B. 터미널 난수(IV)", terminalRandom, BLOCK_SIZE);

        // 3. 평문 생성: PIN + 카드 난수 + 카드 일련번호 결합 후 PKCS7 패딩 적용
        byte[] plaintext = createPlaintext(
                pin.getBytes(StandardCharsets.UTF_8),
                cardRandom,
                cardSerial.getBytes(StandardCharsets.UTF_8)
        );
        printHex("C. 평문 블록(PIN+CardRandom+CSN+Padding)", plaintext, BLOCK_SIZE);

        // 4. SEED CBC 암호화
        byte[] ciphertext = KISA_SEED_CBC.SEED_CBC_Encrypt(masterKey, terminalRandom, plaintext, 0, plaintext.length);
        printHex("D. 암호문 블록", ciphertext, BLOCK_SIZE);

        // 5. 최종 전송 데이터(Payload) 조합: 터미널 난수(IV) + 카드 난수 + 암호문
        String payloadHex = bytesToHex(terminalRandom) + bytesToHex(cardRandom) + bytesToHex(ciphertext);
        printBox("E. 최종 전송 데이터(HEX)");
        System.out.println(payloadHex.length() + " [" + payloadHex + "]\n");

        return payloadHex;
    }

    /**
     * 암호화된 최종 데이터를 복호화하여 PIN, 카드 난수, 카드 일련번호를 분리하고 검증합니다.
     * @param payloadHex 암호화된 최종 16진수 문자열
     * @param pinLength  원본 PIN의 길이 (4~6)
     * @param csnLength  원본 카드 일련번호의 길이
     * @return 복호화된 데이터를 담은 DecodedData 객체
     */
    public DecodedData decryptPin(String payloadHex, int pinLength, int csnLength) {
        if (payloadHex == null || payloadHex.length() < 64)
            throw new IllegalArgumentException("Payload 길이가 너무 짧습니다.");

        printBox("IC카드 PIN 복호화 시작");

        // 1. 수신 데이터 파싱: 터미널 난수, 카드 난수, 암호문 분리
        byte[] terminalRandom = hexToBytes(payloadHex.substring(0, 32));
        byte[] cardRandom = hexToBytes(payloadHex.substring(32, 64));
        byte[] ciphertext = hexToBytes(payloadHex.substring(64));

        printHex("A. 수신 터미널 난수(IV)", terminalRandom, BLOCK_SIZE);
        printHex("B. 수신 카드 난수", cardRandom, BLOCK_SIZE);

        // 2. SEED CBC 복호화
        byte[] decrypted = KISA_SEED_CBC.SEED_CBC_Decrypt(masterKey, terminalRandom, ciphertext, 0, ciphertext.length);
        printHex("C. 복호화된 블록 (패딩 포함)", decrypted, BLOCK_SIZE);

        // 3. PKCS7 패딩 제거
        decrypted = removePkcs7Padding(decrypted);

        // 4. 데이터 분리: 평문에서 PIN, 카드 난수, 카드 일련번호를 각각 추출
        String pin = new String(decrypted, 0, pinLength, StandardCharsets.UTF_8);
        byte[] decryptedCardRandom = Arrays.copyOfRange(decrypted, pinLength, pinLength + BLOCK_SIZE);
        String cardSerial = new String(decrypted, pinLength + BLOCK_SIZE,
                Math.min(csnLength, decrypted.length - pinLength - BLOCK_SIZE), StandardCharsets.UTF_8);

        // 5. 복호화 결과 출력 및 반환
        printBox("D. 복호화 결과 검증");
        System.out.println("PIN: [" + pin + "]");
        System.out.println("CardRandom(HEX): [" + bytesToHex(decryptedCardRandom) + "]");
        System.out.println("CardSerial: [" + cardSerial + "]\n");

        return new DecodedData(pin, decryptedCardRandom, cardSerial);
    }

    // ================== 유틸 ==================
    /** PIN 유효성 검증 (4~6자리) */
    private void validatePin(String pin) {
        if (pin == null || pin.length() < 4 || pin.length() > 6)
            throw new IllegalArgumentException("PIN은 4~6자리여야 합니다.");
    }

    /** 카드 난수 유효성 검증 (16바이트) */
    private void validateCardRandom(byte[] cardRandom) {
        if (cardRandom == null || cardRandom.length != BLOCK_SIZE)
            throw new IllegalArgumentException("Card Random은 16바이트여야 합니다.");
    }

    /** 카드 일련번호(CSN) 유효성 검증 (null 또는 비어있지 않음) */
    private void validateCardSerial(String csn) {
        if (csn == null || csn.isEmpty())
            throw new IllegalArgumentException("Card Serial Number(CSN) 필수");
    }

    /** 평문 데이터를 생성합니다: PIN + 카드 난수 + 카드 일련번호 결합 후 PKCS7 패딩 적용 */
    private byte[] createPlaintext(byte[] pinBytes, byte[] cardRandom, byte[] cardSerialBytes) {
        byte[] combined = new byte[pinBytes.length + cardRandom.length + cardSerialBytes.length];
        System.arraycopy(pinBytes, 0, combined, 0, pinBytes.length);
        System.arraycopy(cardRandom, 0, combined, pinBytes.length, cardRandom.length);
        System.arraycopy(cardSerialBytes, 0, combined, pinBytes.length + cardRandom.length, cardSerialBytes.length);
        return pkcs7Pad(combined, BLOCK_SIZE);
    }

    /** 데이터에 PKCS#7 패딩을 적용합니다. */
    private byte[] pkcs7Pad(byte[] data, int blockSize) {
        int padding = blockSize - (data.length % blockSize);
        byte[] padded = Arrays.copyOf(data, data.length + padding);
        Arrays.fill(padded, data.length, padded.length, (byte) padding);
        return padded;
    }

    /** 데이터에서 PKCS#7 패딩을 제거합니다. */
    private byte[] removePkcs7Padding(byte[] data) {
        int padding = data[data.length - 1] & 0xFF;
        if (padding < 1 || padding > BLOCK_SIZE)
            return data;
        return Arrays.copyOf(data, data.length - padding);
    }

    /** 암호학적으로 안전한 난수 바이트 배열을 생성합니다. */
    private byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    /** 콘솔에 제목을 포함하는 박스를 출력합니다. */
    private void printBox(String title) {
        int totalWidth = 60;
        int padding = totalWidth - getDisplayLength(title) - 2;
        if (padding < 0) padding = 0;
        String border = "═".repeat(totalWidth);
        System.out.println("╔" + border + "╗");
        System.out.println("║ " + title + " ".repeat(padding) + " ║");
        System.out.println("╚" + border + "╝");
    }

    /** 문자의 화면 출력 길이를 계산합니다 (한글/전각 2, 나머지 1). */
    private int getDisplayLength(String str) {
        int length = 0;
        for (char c : str.toCharArray())
            length += (c >= '\uAC00' && c <= '\uD7A3') || (c >= '\u1100' && c <= '\u11FF')
                    || (c >= '\u3130' && c <= '\u318F') ? 2 : 1;
        return length;
    }

    /** 데이터를 블록 단위로 16진수 문자열로 포맷하여 콘솔에 출력합니다. */
    private void printHex(String title, byte[] data, int blockSize) {
        printBox(title);
        for (int i = 0; i < data.length; i += blockSize) {
            int len = Math.min(blockSize, data.length - i);
            byte[] block = Arrays.copyOfRange(data, i, i + len);
            System.out.println("  Block " + String.format("%-2d", (i / blockSize) + 1) + " ║ " + bytesToHex(block));
        }
        System.out.println();
    }

    /** 바이트 배열을 16진수 문자열로 변환합니다. */
    private String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data)
            sb.append(String.format("%02X", b));
        return sb.toString();
    }

    /** 16진수 문자열을 바이트 배열로 변환합니다. */
    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        return data;
    }

    /**
     * 복호화된 데이터를 저장하기 위한 불변(immutable) 데이터 클래스.
     */
    public static class DecodedData {
        public final String pin;
        public final byte[] cardRandom;
        public final String cardSerial;

        /** DecodedData 생성자 */
        public DecodedData(String pin, byte[] cardRandom, String cardSerial) {
            this.pin = pin;
            this.cardRandom = cardRandom;
            this.cardSerial = cardSerial;
        }
    }

    // ================== 테스트 ==================
    public static void main(String[] args) {
        // 1. 테스트용 마스터 키로 Crypto 객체 생성
        String masterKeyHex = "00112233445566778899AABBCCDDEEFF";
        IcCardPinCryptoWithCSN crypto = new IcCardPinCryptoWithCSN(masterKeyHex);

        // 2. 테스트용 PIN, 카드 난수, 카드 일련번호 생성
        String pin = "1234";
        byte[] cardRandom = crypto.generateRandomBytes(BLOCK_SIZE);
        String cardSerial = "IC1234567890";

        // 3. 암호화 실행
        String payloadHex = crypto.encryptPin(pin, cardRandom, cardSerial);
        // 4. 복호화 실행 및 결과 검증
        crypto.decryptPin(payloadHex, 4, cardSerial.length());
    }
}
