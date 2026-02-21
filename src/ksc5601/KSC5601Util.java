package ksc5601;

import java.nio.charset.Charset;

public class KSC5601Util {
    private static final Charset MS949 = Charset.forName("MS949");
    private static final char FULL_WIDTH_SPACE = '\u3000'; // 전각 스페이스

    // 유틸리티 클래스의 생성자를 숨김
    private KSC5601Util() {
        throw new UnsupportedOperationException("Utility class");
    }

    // 전각 문자 변환 메소드
    public static String convertToFullWidth(String input) {
        System.out.println("전각 문자 변환 시작...");
        StringBuilder fullWidthStr = new StringBuilder();
        for (char ch : input.toCharArray()) {
            fullWidthStr.append(convertCharToFullWidth(ch));
        }
        System.out.printf("전각 문자 변환 완료: [%s]%n", fullWidthStr);
        return fullWidthStr.toString();
    }

    // 문자를 전각 문자로 변환
    private static char convertCharToFullWidth(char ch) {
        if (ch == 0x20) {
            return 0x3000; // 공백을 전각 공백으로 변환
        } else if (ch >= 0x21 && ch <= 0x7E) {
            return (char) (ch + 0xFEE0); // 반각 ASCII -> 전각
        }
        return ch; // 한글, 기호는 그대로 유지
    }

    // KSC5601 필터링 메소드
    public static String filterKSC(String input) {
        System.out.println("KSC5601 필터링 시작...");
        StringBuilder filteredStr = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (isKSC(ch)) {
                filteredStr.append(ch); // KSC5601 범위에 있는 문자를 추가
            }
        }
        System.out.printf("KSC5601 필터링 완료: [%s]%n", filteredStr);

        // KSC5601 필터링된 문자열의 길이 계산
        int lengthInMS949 = getLengthInMS949(filteredStr.toString());
        System.out.printf("KSC5601 필터링 완료 길이: [%d]%n", lengthInMS949);
        return filteredStr.toString();
    }

    // KSC5601 범위 필터링
    private static boolean isKSC(char ch) {
        byte[] bytes = String.valueOf(ch).getBytes(MS949);
        if (bytes.length == 1) {
            return false; // 한 바이트 문자는 제외
        }

        int byte1 = bytes[0] & 0xFF;
        int byte2 = bytes[1] & 0xFF;
        int combinedBytes = (byte1 << 8) | byte2;

        // KSC5601 범위 필터링
        boolean isValid = (combinedBytes >= 0xA1A1 && combinedBytes <= 0xA2FE) || // 구두점 및 기호
                (combinedBytes >= 0xA3A1 && combinedBytes <= 0xA3FE) || // 전각 ASCII
                (combinedBytes >= 0xA4A1 && combinedBytes <= 0xA4FE) || // 2벌식 한글 자음 모음
                (combinedBytes >= 0xB0A1 && combinedBytes <= 0xC8FE) || // 한글 음절
                (combinedBytes >= 0xCAA1 && combinedBytes <= 0xFDFE); // 한자
        System.out.printf("문자 '[%s]' 필터링 결과: [%b]%n", ch, isValid);
        return isValid;
    }

    // MS949 기준 문자열 길이 계산 메소드
    public static int getLengthInMS949(String input) {
        byte[] bytes = input.getBytes(MS949);
        return bytes.length; // 바이트 길이 반환
    }

    // 패딩 메소드
    public static String padString(String input, int maxByteLength) {
        System.out.println("패딩 시작...");
        StringBuilder paddedStr = new StringBuilder(input);
        byte[] byteArray = paddedStr.toString().getBytes(MS949);

        // 바이트 길이가 최대 길이를 초과하면 잘라줌
        if (byteArray.length > maxByteLength) {
            int cutIndex = calculateCutIndex(paddedStr.toString(), maxByteLength);
            paddedStr.setLength(cutIndex); // 잘라내기
            System.out.printf("잘린 후 문자열: [%s]%n", paddedStr);
            System.out.printf("잘린 후 바이트 길이: [%d]%n", paddedStr.toString().getBytes(MS949).length);
        }

        // 부족한 바이트는 전각 스페이스로 채움
        while (paddedStr.toString().getBytes(MS949).length < maxByteLength) {
            paddedStr.append(FULL_WIDTH_SPACE);
        }
        System.out.printf("패딩 후 문자열: [%s]%n", paddedStr);
        return paddedStr.toString();
    }

    // 잘라야 할 인덱스를 계산하는 메소드
    private static int calculateCutIndex(String str, int maxByteLength) {
        int byteCount = 0;
        for (int i = 0; i < str.length(); i++) {
            byte[] bytes = String.valueOf(str.charAt(i)).getBytes(MS949);
            byteCount += bytes.length;
            if (byteCount > maxByteLength) {
                return i; // 잘라야 할 인덱스 반환
            }
        }
        return str.length(); // 전체 길이 반환
    }

    // 헥사값 출력 메소드
    public static String printHex(String input) {
        System.out.println("헥사값 출력 시작...");
        StringBuilder hexStr = new StringBuilder();
        byte[] byteArray = input.getBytes(MS949);
        for (byte b : byteArray) {
            hexStr.append(String.format("%02X ", b & 0xFF)); // 헥사값으로 변환
        }
        String hexOutput = hexStr.toString().trim();
        System.out.printf("헥사값 출력 완료: [%s]%n", hexOutput);
        return hexOutput;
    }

    // 전각 문자 변환, KSC5601 필터링, 패딩을 적용하는 메소드
    public static String convertToFullWidthFilterAndPad(String input, int maxByteLength) {
        String fullWidthStr = convertToFullWidth(input); // 전각 문자 변환
        String filteredStr = filterKSC(fullWidthStr); // KSC5601 필터링
        return padString(filteredStr, maxByteLength); // 패딩 적용 후 반환
    }

    public static String convertToHalfWidthAndTrimRight(String src) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            // 전각 영문자 또는 특수문자 -> 반각 변환 (헥사값 사용)
            if (c >= 0xFF01 && c <= 0xFF5E) {
                c -= 0xFEE0; // 16진수로 전각 -> 반각 변환
            }
            // 전각 공백 -> 반각 공백 변환 (헥사값 사용)
            else if (c == 0x3000) {
                c = 0x20; // 전각 공백 -> 반각 공백
            }
            strBuilder.append(c);
        }
    
        // 오른쪽 공백을 제거하는 rtrim
        int length = strBuilder.length();
        while (length > 0 && strBuilder.charAt(length - 1) == 0x20) {
            strBuilder.deleteCharAt(length - 1);
            length--;
        }
    
        return strBuilder.toString();
    }
    
    
}
