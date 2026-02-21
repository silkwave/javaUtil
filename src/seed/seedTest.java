package seed;

public class seedTest {

    private static final int BLOCK_SIZE = 16;

    /**
     * SEED-CBC 암호화 (수동 구현)
     * KISA_SEED 클래스의 기본 블록 암호화 기능을 사용하여 CBC 모드를 직접 구현한 메서드입니다.
     */
    private static byte[] encryptCBC(byte[] key, byte[] iv, byte[] plainText) {
        if (plainText == null || plainText.length == 0) {
            return null;
        }

        // 1. 데이터 패딩 (PKCS#7)
        int len = plainText.length;
        int padding = BLOCK_SIZE - (len % BLOCK_SIZE);
        int paddedLen = len + padding;
        byte[] paddedPlainText = new byte[paddedLen];
        
        System.arraycopy(plainText, 0, paddedPlainText, 0, len);
        for (int i = 0; i < padding; i++) {
            paddedPlainText[len + i] = (byte) padding;
        }

        // 2. 변수 초기화
        byte[] cbcVector = initIv(iv); // IV 복사
        byte[] result = new byte[paddedLen];
        int blockCount = paddedLen / BLOCK_SIZE;

        // 3. 키 스케줄링
        int[] roundKey = new int[32];
        KISA_SEED seedEngine = new KISA_SEED();
        seedEngine.SEED_KeySched(key, roundKey);

        // 4. 블록 암호화 수행
        byte[] inputBlock = new byte[BLOCK_SIZE];
        
        for (int i = 0; i < blockCount; i++) {
            // 현재 블록 복사
            System.arraycopy(paddedPlainText, i * BLOCK_SIZE, inputBlock, 0, BLOCK_SIZE);

            // XOR: 평문 XOR IV (또는 이전 암호문)
            byte[] xoredBlock = xor(inputBlock, cbcVector);

            // SEED 암호화 (byte[] -> int[] -> Encrypt -> byte[])
            int[] inBuffer = KISA_SEED_CBC.chartoint32_for_SEED_CBC(xoredBlock, BLOCK_SIZE);
            int[] outBuffer = new int[4];
            seedEngine.SEED_Encrypt(outBuffer, inBuffer, roundKey);
            byte[] outputBlock = KISA_SEED_CBC.int32tochar_for_SEED_CBC(outBuffer, BLOCK_SIZE);

            // 결과 저장
            System.arraycopy(outputBlock, 0, result, i * BLOCK_SIZE, BLOCK_SIZE);
            
            // 다음 라운드를 위해 IV 업데이트 (현재 암호문이 다음 블록의 IV가 됨)
            System.arraycopy(outputBlock, 0, cbcVector, 0, BLOCK_SIZE);
        }

        return result;
    }

    /**
     * SEED-CBC 복호화 (수동 구현)
     */
    private static byte[] decryptCBC(byte[] key, byte[] iv, byte[] encryptedText) {
        if (encryptedText == null || encryptedText.length == 0 || encryptedText.length % BLOCK_SIZE != 0) {
            return null;
        }

        int len = encryptedText.length;
        byte[] cbcVector = initIv(iv);
        byte[] result = new byte[len];
        int blockCount = len / BLOCK_SIZE;

        // 키 스케줄링
        int[] roundKey = new int[32];
        KISA_SEED seedEngine = new KISA_SEED();
        seedEngine.SEED_KeySched(key, roundKey);

        byte[] ciphertextBlock = new byte[BLOCK_SIZE];

        for (int i = 0; i < blockCount; i++) {
            // 현재 암호문 블록 복사
            System.arraycopy(encryptedText, i * BLOCK_SIZE, ciphertextBlock, 0, BLOCK_SIZE);

            // SEED 복호화
            int[] inBuffer = KISA_SEED_CBC.chartoint32_for_SEED_CBC(ciphertextBlock, BLOCK_SIZE);
            int[] outBuffer = new int[4];
            seedEngine.SEED_Decrypt(outBuffer, inBuffer, roundKey);
            byte[] decryptedBlock = KISA_SEED_CBC.int32tochar_for_SEED_CBC(outBuffer, BLOCK_SIZE);

            // XOR: 복호화된 블록 XOR IV (또는 이전 암호문)
            byte[] plaintextBlock = xor(decryptedBlock, cbcVector);
            
            System.arraycopy(plaintextBlock, 0, result, i * BLOCK_SIZE, BLOCK_SIZE);
            
            // 다음 라운드를 위해 IV 업데이트 (현재 암호문 블록이 다음의 IV가 됨)
            System.arraycopy(ciphertextBlock, 0, cbcVector, 0, BLOCK_SIZE);
        }

        // PKCS#7 Unpadding
        int padding = result[len - 1] & 0xFF;
        if (padding < 1 || padding > BLOCK_SIZE) {
            return result; // 패딩 오류 시 원본 반환 (또는 예외 처리)
        }
        
        byte[] unpaddedResult = new byte[len - padding];
        System.arraycopy(result, 0, unpaddedResult, 0, len - padding);
        return unpaddedResult;
    }

    /**
     * IV 초기화 유틸리티
     */
    private static byte[] initIv(byte[] iv) {
        byte[] newIv = new byte[BLOCK_SIZE];
        if (iv != null && iv.length == BLOCK_SIZE) {
            System.arraycopy(iv, 0, newIv, 0, BLOCK_SIZE);
        }
        return newIv;
    }

    /**
     * 바이트 배열 간 XOR 연산 수행 (길이는 BLOCK_SIZE로 고정)
     */
    private static byte[] xor(byte[] source, byte[] vector) {
        byte[] result = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            result[i] = (byte) (source[i] ^ vector[i]);
        }
        return result;
    }

    private static String toHex(byte[] data) {
        if (data == null) return "";
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    // 헥사 문자열 -> 문자열 변환
    public static String hexToString(String hex) {
        hex = hex.replace(" ", ""); // 공백 제거
        int length = hex.length();
        byte[] byteArray = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            byteArray[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return new String(byteArray);
    }

    private static void printHex(String title, byte[] data) {
        System.out.print(title + " [" + (data != null ? data.length : 0) + " bytes]: ");
        if (data != null) {
            for (byte b : data) {
                System.out.printf("%02X ", b);
            }
        }
        System.out.println();
    }

    private static byte[] fromHex(String hex) {
        int len = hex.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string");
        }

        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            out[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return out;
    }

    public static void main(String[] args) {

        String keyString = "F26B51BCBB1C5321D26E8B78FAC35F8C";
        String ivString  = "9132597362B3BEE478FA53A9BB47EC7C";
        byte[] key = fromHex(keyString);
        byte[] iv = fromHex(ivString);

        byte[] plainText = "1234444".getBytes(); // PG에서 명시한 IV

        System.out.println("========================================");
        System.out.println("SEED CBC Manual Implementation Test");
        System.out.println("========================================");

        printHex("Key", key);
        printHex("IV", iv);
        printHex("Plain Text", plainText);

        // 암호화 수행
        byte[] encrypted = encryptCBC(key, iv, plainText);
        printHex("Encrypted", encrypted);

        // 복호화 수행
        byte[] decrypted = decryptCBC(key, iv, encrypted);
        printHex("Decrypted", decrypted);

        System.out.println("plainText  " + hexToString(toHex(decrypted)));

        boolean match = java.util.Arrays.equals(plainText, decrypted);
        System.out.println("Result: " + (match ? "SUCCESS" : "FAILURE"));
    }

}
