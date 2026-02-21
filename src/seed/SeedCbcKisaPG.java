package seed;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * 금융결제원(KFTC) PG 시스템의 현금IC카드 PIN 암호화 표준을 준수하는 예제 클래스입니다.
 * 이 클래스는 SEED-CBC 알고리즘과 PKCS#7 패딩을 사용하여 PIN을 안전하게 암호화하고,
 * 최종 전송 데이터(Payload)를 생성하는 전체 과정을 보여줍니다.
 *
 * <h3>암호화 명세</h3>
 * <ul>
 * <li><b>암호화 알고리즘:</b> SEED-CBC (Cipher Block Chaining)</li>
 * <li><b>패딩 방식:</b> PKCS#7</li>
 * <li><b>초기화 벡터(IV):</b> 터미널에서 생성된 16바이트 난수</li>
 * <li><b>평문 구성:</b> PIN (4~6바이트) + 카드 난수 (16바이트)</li>
 * <li><b>최종 전송 데이터:</b> 터미널 난수(IV) || 카드 난수 || 암호문 (모두 16진수 문자열)</li>
 * </ul>
 */
public class SeedCbcKisaPG {

    private static final int BLOCK_SIZE_BYTES = 16;
    private static final int IV_LENGTH_BYTES = 16;
    private static final int CARD_RANDOM_LENGTH_BYTES = 16;
    private static final String DEFAULT_MASTER_KEY_HEX = "00112233445566778899AABBCCDDEEFF";
    private static final String DEFAULT_PIN = "1234";
    private static final int MIN_PIN_LENGTH = 4;

    private static final int IV_HEX_LENGTH = IV_LENGTH_BYTES * 2;
    private static final int CARD_RANDOM_HEX_LENGTH = CARD_RANDOM_LENGTH_BYTES * 2;
    private static final int MIN_CIPHERTEXT_HEX_LENGTH = BLOCK_SIZE_BYTES * 2;
    private static final int MIN_PAYLOAD_HEX_LENGTH = IV_HEX_LENGTH + CARD_RANDOM_HEX_LENGTH + MIN_CIPHERTEXT_HEX_LENGTH;

    private static final int PAYLOAD_IV_END_INDEX = IV_HEX_LENGTH;
    private static final int PAYLOAD_CARD_RANDOM_END_INDEX = IV_HEX_LENGTH + CARD_RANDOM_HEX_LENGTH;

    private static final int PRINT_BOX_WIDTH = 70;

    private final byte[] masterKey;

    public SeedCbcKisaPG() {
        // === 1. 마스터 키 (HSM에서 추출 또는 KMS 관리) ===
        // 실제 운영 환경에서는 이 키를 HSM(Hardware Security Module)이나 KMS(Key Management Service)를
        // 통해 안전하게 관리해야 합니다.
        this.masterKey = hexToBytes(DEFAULT_MASTER_KEY_HEX);
    }

    /**
     * PIN 암호화의 전체 흐름을 실행하고, 최종적으로 서버에 전송될 16진수 문자열 페이로드를 생성합니다.
     * 
     * @return 터미널 난수(IV) + 카드 난수 + 암호문이 결합된 16진수 문자열 페이로드
     */
    public String encrypt() {
        printBox("금융결제원 PG SEED-CBC PIN 암호화 시작");

        printHex("1. 마스터 키", masterKey, BLOCK_SIZE_BYTES);

        // === 2. 터미널 난수 (IV 역할) ===
        // CBC 모드에서 첫 블록을 암호화할 때 사용되는 16바이트 초기화 벡터(IV)입니다.
        // 매 암호화마다 새롭게 생성해야 합니다.
        byte[] iv = generateRandomBytes(IV_LENGTH_BYTES);
        printHex("2. 터미널 난수 (IV)", iv, BLOCK_SIZE_BYTES);

        // === 3. 카드 난수 (평문에 포함) ===
        byte[] cardRandom = generateRandomBytes(CARD_RANDOM_LENGTH_BYTES);
        printHex("3. 카드 난수", cardRandom, BLOCK_SIZE_BYTES);

        // === 4. PIN 입력 (4~6자리) ===
        String pin = DEFAULT_PIN; // 실제 운영에서는 사용자로부터 안전하게 입력받아야 합니다.
        byte[] pinBytes = pin.getBytes(StandardCharsets.UTF_8);
        print("4. 입력 PIN", pin);

        // === 5. 평문 구성: PIN + CardRandom + PKCS7 패딩 ===
        byte[] plaintext = createPlaintext(pinBytes, cardRandom);
        printHex("5. 평문 블록 (PIN + CardRandom + Padding)", plaintext, BLOCK_SIZE_BYTES);

        // === 6. SEED-CBC 암호화 (KISA 구현) ===
        // KISA에서 제공하는 라이브러리를 사용하여 암호화를 수행합니다.
        byte[] ciphertext = KISA_SEED_CBC.SEED_CBC_Encrypt(masterKey, iv, plaintext, 0, plaintext.length);
        printHex("6. 암호문 블록", ciphertext, BLOCK_SIZE_BYTES);

        // === 7. 최종 전송 데이터: IV || CardRandom || Ciphertext ===
        String payloadHex = bytesToHex(iv) + bytesToHex(cardRandom) + bytesToHex(ciphertext);
        printBox("7. 최종 전송 데이터 (HEX)");
        System.out.println(payloadHex.length() + " [" + payloadHex + "]\n");

        return payloadHex;
    }

    /**
     * 암호화된 페이로드를 받아 복호화하고, 원본 데이터를 검증합니다.
     * 
     * @param payloadHex 암호화된 16진수 문자열 페이로드
     */
    public void decrypt(String payloadHex) {
        printBox("복호화 및 검증 시작");

        // 최소 길이 검증: IV(32) + CardRandom(32) + 암호문 최소 1블록(32) = 96
        if (payloadHex.length() < MIN_PAYLOAD_HEX_LENGTH) {
            throw new IllegalArgumentException("Invalid payload length");
        }

        // 1. 페이로드 파싱: IV, 카드 난수, 암호문 분리
        byte[] iv = hexToBytes(payloadHex.substring(0, PAYLOAD_IV_END_INDEX));
        byte[] receivedCardRandom = hexToBytes(payloadHex.substring(PAYLOAD_IV_END_INDEX, PAYLOAD_CARD_RANDOM_END_INDEX));
        byte[] encrypted = hexToBytes(payloadHex.substring(PAYLOAD_CARD_RANDOM_END_INDEX));

        printHex("수신 IV (터미널 난수)", iv, BLOCK_SIZE_BYTES);
        printHex("수신 카드 난수", receivedCardRandom, BLOCK_SIZE_BYTES);

        // 2. SEED-CBC 복호화
        byte[] decryptedPadded = KISA_SEED_CBC.SEED_CBC_Decrypt(
                masterKey, iv, encrypted, 0, encrypted.length);
        printHex("복호화된 블록 (패딩 포함)", decryptedPadded, BLOCK_SIZE_BYTES);

        // PKCS7 패딩 제거
        byte[] decrypted = pkcs7Unpad(decryptedPadded);
        printHex("패딩 제거 후 평문", decrypted, BLOCK_SIZE_BYTES);

        // 4. 데이터 분리 및 검증: PIN과 카드 난수 추출
        int pinLength = 0;
        for (int i = 0; i < decrypted.length - CARD_RANDOM_LENGTH_BYTES; i++) {
            if (decrypted[i] >= '0' && decrypted[i] <= '9') {
                pinLength++;
            } else
                break;
        }
        if (pinLength == 0)
            pinLength = MIN_PIN_LENGTH; // PIN 길이를 특정할 수 없는 경우, 최소 길이로 가정

        String recoveredPin = new String(decrypted, 0, pinLength, StandardCharsets.UTF_8);
        byte[] recoveredCardRandom = Arrays.copyOfRange(decrypted, pinLength, pinLength + CARD_RANDOM_LENGTH_BYTES);

        printBox("복호화 결과");
        System.out.println("복구된 PIN: [" + recoveredPin + "]");
        System.out.println("복구된 CardRandom: [" + bytesToHex(recoveredCardRandom) + "]\n");
    }

    // ====================== PKCS7 패딩 ======================
    /**
     * 데이터에 PKCS#7 패딩을 추가합니다.
     * 데이터 길이를 블록 크기의 배수로 맞추고, 마지막 블록의 남은 공간을 패딩할 바이트 수로 채웁니다.
     * 
     * @param data 패딩할 원본 데이터
     */
    private byte[] pkcs7Pad(byte[] data) {
        int blockSize = BLOCK_SIZE_BYTES;
        int padLen = blockSize - (data.length % blockSize);
        byte[] padded = new byte[data.length + padLen];
        System.arraycopy(data, 0, padded, 0, data.length);
        Arrays.fill(padded, data.length, padded.length, (byte) padLen);
        return padded;
    }

    /**
     * 데이터에서 PKCS#7 패딩을 제거합니다.
     * 마지막 바이트 값을 읽어 패딩 길이를 확인하고, 해당 길이만큼 데이터 끝에서 제거합니다.
     * 
     * @param data 패딩이 포함된 데이터
     */
    private byte[] pkcs7Unpad(byte[] data) {
        if (data.length == 0)
            return data;
        int padLen = data[data.length - 1] & 0xFF;
        if (padLen < 1 || padLen > BLOCK_SIZE_BYTES) {
            throw new IllegalArgumentException("Invalid PKCS7 padding");
        }
        return Arrays.copyOf(data, data.length - padLen);
    }

    // ====================== 평문 구성 ======================
    /**
     * 암호화할 평문을 생성합니다.
     * PIN 바이트와 카드 난수 바이트를 결합한 후, PKCS#7 패딩을 적용합니다.
     * 
     * @param pinBytes   PIN 바이트 배열
     * @param cardRandom 카드 난수 바이트 배열
     * @return 패딩이 적용된 최종 평문 데이터
     */
    private byte[] createPlaintext(byte[] pinBytes, byte[] cardRandom) {
        byte[] combined = new byte[pinBytes.length + cardRandom.length];
        System.arraycopy(pinBytes, 0, combined, 0, pinBytes.length);
        System.arraycopy(cardRandom, 0, combined, pinBytes.length, cardRandom.length);
        return pkcs7Pad(combined); // KISA 라이브러리와 달리, 여기서는 명시적으로 패딩을 호출합니다.
    }

    // ====================== 유틸리티 ======================
    /** 콘솔에 제목을 포함하는 박스를 출력합니다. */
    private void printBox(String title) {
        int width = PRINT_BOX_WIDTH;
        String border = "═".repeat(width);
        System.out.println("╔" + border + "╗");
        System.out.println("║ " + center(title, width - 2) + " ║");
        System.out.println("╚" + border + "╝");
    }

    /** 문자열을 주어진 너비의 중앙에 정렬합니다. (한글 등 전각 문자 너비 고려) */
    private String center(String text, int len) {
        int pad = (len - getDisplayLength(text)) / 2;
        return " ".repeat(Math.max(0, pad)) + text + " ".repeat(Math.max(0, len - pad - getDisplayLength(text)));
    }

    /** 문자의 화면 출력 길이를 계산합니다 (한글/전각 2, 나머지 1). */
    private int getDisplayLength(String str) {
        int len = 0;
        for (char c : str.toCharArray()) {
            len += (c >= 0xAC00 && c <= 0xD7A3) ? 2 : 1;
        }
        return len;
    }

    /** 데이터를 블록 단위로 16진수 문자열로 포맷하여 콘솔에 출력합니다. */
    private void printHex(String title, byte[] data, int blockSize) {
        printBox(title);
        for (int i = 0; i < data.length; i += blockSize) {
            int len = Math.min(blockSize, data.length - i);
            byte[] block = Arrays.copyOfRange(data, i, i + len);
            System.out.printf("  Block %2d ║ %s%n", (i / blockSize) + 1, bytesToHex(block));
        }
        System.out.println();
    }

    /** 일반 문자열 값을 제목과 함께 박스 형태로 출력합니다. */
    private void print(String title, String value) {
        printBox(title);
        System.out.println("  " + value + "\n");
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
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    /** 암호학적으로 안전한 난수 바이트 배열을 생성합니다. */
    private byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    // ====================== 메인 ======================
    public static void main(String[] args) {
        // 1. 암호화 객체 생성
        SeedCbcKisaPG pg = new SeedCbcKisaPG();
        // 2. 암호화 실행
        String payload = pg.encrypt();
        // 3. 생성된 페이로드로 복호화 실행 및 검증
        pg.decrypt(payload);
    }
}