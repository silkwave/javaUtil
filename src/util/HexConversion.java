package util;

import java.nio.charset.Charset;
import java.util.Arrays;

public class HexConversion {

    private static final char[] HEX_CHAR = "0123456789ABCDEF".toCharArray();

    public static void main(String[] args) {
        String hexString = "FFFF";  // 변환할 헥사 문자열

        // 1. 헥사 문자열 -> 바이트 배열
        byte[] byteArray = hexStringToByteArray(hexString);
        System.out.println("Byte array: " + Arrays.toString(byteArray)); // 결과: Byte array: [-1, -1]

        // 2. 바이트 배열 -> 헥사 문자열
        String resultHexString = byteArrayToHexString(byteArray);
        System.out.println("Converted back to hex: " + resultHexString); // 결과: Converted back to hex: FFFF

        String normalString = "01";  // 헥사로 변환할 일반 문자열

        // 3. 헥사 문자열 -> 일반 문자열 (MS949 인코딩)
        String convertedString = hexToString(normalString);
        System.out.println("Hex to String: " + convertedString);  // 결과: Hex to String: (유효하지 않은 문자일 경우)

        // 4. 일반 문자열 -> 헥사 문자열
        String hexFromString = stringToHex(convertedString);
        System.out.println("String to Hex: " + hexFromString); // 결과: String to Hex: (변환된 헥사 값)
    }

    // 헥사 문자열 -> 바이트 배열 변환
    public static byte[] hexStringToByteArray(String hex) {
        hex = hex.replace(" ", "");
        int length = hex.length();
        byte[] byteArray = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            byteArray[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return byteArray;
    }

    // 바이트 배열 -> 헥사 문자열 변환
    public static String byteArrayToHexString(byte[] byteArray) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray) {
            hexString.append(HEX_CHAR[(b >> 4) & 0x0F]);
            hexString.append(HEX_CHAR[b & 0x0F]); // 바이트를 2자리 헥사로 변환
        }
        return hexString.toString().toUpperCase();
    }

    // 헥사 문자열 -> 문자열 변환 (MS949 인코딩 사용)
    public static String hexToString(String hexString) {
        byte[] byteArray = hexStringToByteArray(hexString);
        System.out.println("hexToString Byte array: " + Arrays.toString(byteArray));
        
        // 바이트 배열을 MS949 인코딩으로 변환
        return new String(byteArray, Charset.forName("MS949"));   
    }

    // 일반 문자열 -> 헥사 문자열 변환 (MS949 인코딩 사용)
    public static String stringToHex(String input) {
        byte[] byteArray = input.getBytes(Charset.forName("MS949")); // 문자열을 MS949로 변환하여 바이트 배열로
        StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray) {
            hexString.append(HEX_CHAR[(b >> 4) & 0x0F]);
            hexString.append(HEX_CHAR[b & 0x0F]); // 바이트를 2자리 헥사로 변환
        }
        return hexString.toString().toUpperCase();
    }
}