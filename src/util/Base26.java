package util;

import java.util.HashMap;
import java.util.Map;

public class Base26 {

    // 10진수를 26진수로 변환 (0-9, A-P까지)
    private static final char[] BASE26_CHARS = "0123456789ABCDEFGHIJKLMNOP".toCharArray();
    
    private static final Map<Character, Integer> CHAR_TO_DECIMAL_MAP = new HashMap<>();

    static {
        for (int i = 0; i < BASE26_CHARS.length; i++) {
            CHAR_TO_DECIMAL_MAP.put(BASE26_CHARS[i], i);
        }
    }

    private Base26() {
        throw new UnsupportedOperationException("Utility class");
    }

    // 10진수를 26진수로 변환 (0-9, A-P까지)
    public static String toBase26(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("음수는 변환할 수 없습니다.");
        }
        if (number == 0) {
            return "0";
        }

        StringBuilder result = new StringBuilder();
        while (number > 0) {
            result.append(BASE26_CHARS[number % 26]);
            number /= 26;
        }
        return result.reverse().toString();
    }

    // 26진수를 10진수로 변환 (0-9, A-P까지)
    public static int toDecimal(String base26) {
        if (base26 == null || base26.isEmpty()) {
            throw new IllegalArgumentException("입력값이 비어 있거나 null입니다.");
        }

        int result = 0;
        for (int i = 0; i < base26.length(); i++) {
            char c = base26.charAt(i);
            Integer value = CHAR_TO_DECIMAL_MAP.get(c);
            if (value == null) {
                throw new IllegalArgumentException("유효하지 않은 문자입니다. 0-9, A-P만 가능합니다: " + c);
            }
            result = result * 26 + value;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("--- 기본 변환 테스트 ---");
        // 10진수 -> 26진수
        int number1 = 456975;
        String base26_1 = toBase26(number1);
        System.out.println("10진수 " + number1 + " -> 26진수: " + base26_1); // 예상: PPPP

        // 26진수 -> 10진수
        String base26_2 = "PPPP";
        int decimal1 = toDecimal(base26_2);
        System.out.println("26진수 " + base26_2 + " -> 10진수: " + decimal1); // 예상: 456975

        System.out.println("\n--- 경계값 및 자릿수 변경 테스트 ---");
        // 0 처리
        System.out.println("10진수 0 -> 26진수: " + toBase26(0)); // 예상: 0
        // 한 자릿수 최대값
        System.out.println("10진수 25 -> 26진수: " + toBase26(25)); // 예상: P
        // 자릿수 올림
        System.out.println("10진수 26 -> 26진수: " + toBase26(26)); // 예상: 10
        // 숫자와 문자가 섞이는 경우
        System.out.println("10진수 36 -> 26진수: " + toBase26(36)); // 예상: 1A
        System.out.println("26진수 1A -> 10진수: " + toDecimal("1A")); // 예상: 36

        System.out.println("\n--- 추가 변환 예제 ---");
        int number2 = 123456;
        String base26_3 = toBase26(number2);
        int decimal2 = toDecimal(base26_3);
        System.out.println("10진수 " + number2 + " -> 26진수: " + base26_3); // 예상: 71I4
        System.out.println("26진수 " + base26_3 + " -> 10진수: " + decimal2); // 예상: 123456

        System.out.println("\n--- 오류 처리 테스트 ---");
        try {
            // 유효하지 않은 문자가 포함된 경우
            System.out.print("toDecimal(\"PPQ\") 호출 시: ");
            toDecimal("PPQ");
        } catch (IllegalArgumentException e) {
            System.err.println("오류 발생! -> " + e.getMessage());
        }

        try {
            // 음수를 변환하려는 경우
            System.out.print("toBase26(-10) 호출 시: ");
            toBase26(-10);
        } catch (IllegalArgumentException e) {
            System.err.println("오류 발생! -> " + e.getMessage());
        }
    }
}
