package util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Arrays;

public class Aes128Util {

    // AES128 = key 16byte
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * AES128 암호화
     */
    public static String encrypt(String plainText, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * AES128 복호화
     */
    public static String decrypt(String cipherText, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /*
     * =========================
     * Hex 변환 유틸
     * =========================
     */

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string length: " + len);
        }

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return data;
    }

    /**
     * 테스트용 main
     */
    public static void main(String[] args) throws Exception {
        String key = "1234567890123456"; // 16byte
        String iv = "abcdef9876543210"; // 16byte

        String text = "Hello AES128!";

        System.out.println("--- AES Encryption/Decryption Test ---");
        // 암호화
        String enc = encrypt(text, key, iv);
        System.out.println("Encrypted: " + enc);

        // 복호화
        String dec = decrypt(enc, key, iv);
        System.out.println("Decrypted: " + dec);

        System.out.println("\n--- Hex-Byte Conversion Test ---");
        byte[] originalBytes = "Test Data 123!@#".getBytes(StandardCharsets.UTF_8);
        System.out.println("Original bytes: " + Arrays.toString(originalBytes));

        // byte[] -> hex
        String hexString = bytesToHex(originalBytes);
        System.out.println("Bytes to Hex: " + hexString);

        // hex -> byte[]
        byte[] convertedBytes = hexToBytes(hexString);
        System.out.println("Hex to Bytes: " + Arrays.toString(convertedBytes));

        // Verify
        System.out.println("Conversion successful: " + Arrays.equals(originalBytes, convertedBytes));
    }
}
