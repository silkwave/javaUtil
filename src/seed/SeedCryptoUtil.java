package seed;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * 금융/PG 표준 SEED 암복호화 유틸
 *
 * Algorithm : SEED
 * Mode : CBC
 * Padding : PKCS5Padding
 * Key Length : 16 bytes (128 bit)
 * IV Length : 16 bytes
 */
public final class SeedCryptoUtil {

    private SeedCryptoUtil() {
        // util class
    }

    // =======================
    // 1. byte[] <-> byte[]
    // =======================

    public static byte[] encrypt(
            byte[] key,
            byte[] iv,
            byte[] plain) throws Exception {

        validate(key, iv);

        return KISA_SEED_CBC.SEED_CBC_Encrypt(key, iv, plain, 0, plain.length);
    }

    public static byte[] decrypt(
            byte[] key,
            byte[] iv,
            byte[] encrypted) throws Exception {

        validate(key, iv);

        byte[] decrypted = KISA_SEED_CBC.SEED_CBC_Decrypt(key, iv, encrypted, 0, encrypted.length);
        if (decrypted == null) {
            throw new IllegalArgumentException("Decryption failed");
        }
        return decrypted;
    }

    // =======================
    // 2. HEX
    // =======================

    public static String encryptHex(
            byte[] key,
            byte[] iv,
            String plain,
            Charset charset) throws Exception {

        byte[] enc = encrypt(key, iv, plain.getBytes(charset));
        return toHex(enc);
    }

    public static String decryptHex(
            byte[] key,
            byte[] iv,
            String hex,
            Charset charset) throws Exception {

        byte[] dec = decrypt(key, iv, fromHex(hex));
        return new String(dec, charset);
    }

    // =======================
    // 3. Base64
    // =======================

    public static String encryptBase64(
            byte[] key,
            byte[] iv,
            String plain,
            Charset charset) throws Exception {

        byte[] enc = encrypt(key, iv, plain.getBytes(charset));
        return Base64.getEncoder().encodeToString(enc);
    }

    public static String decryptBase64(
            byte[] key,
            byte[] iv,
            String base64,
            Charset charset) throws Exception {

        byte[] dec = decrypt(key, iv, Base64.getDecoder().decode(base64));
        return new String(dec, charset);
    }

    // =======================
    // 4. Validate
    // =======================

    private static void validate(byte[] key, byte[] iv) {
        if (key == null || key.length != 16) {
            throw new IllegalArgumentException("SEED key must be 16 bytes");
        }
        if (iv == null || iv.length != 16) {
            throw new IllegalArgumentException("SEED IV must be 16 bytes");
        }
    }

    // =======================
    // 5. HEX Utils
    // =======================

    private static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
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
}
