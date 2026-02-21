package util;

public class Base26Converter {

    // 10진수를 26진수로 변환 (0-9, A-P까지)
    public static String toBase26(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("음수는 변환할 수 없습니다.");
        }
        if (number == 0) {
            return "0"; // 0은 '0'으로 반환
        }

        StringBuilder result = new StringBuilder();
        while (number > 0) {
            result.insert(0, convertToBase26Char(number % 26));
            number = number / 26;  // 다음 자리 계산
        }

        return result.toString();
    }

    // 26진수의 나머지를 문자로 변환 (0-9는 '0'-'9', 10-25는 'A'-'P')
    private static char convertToBase26Char(int remainder) {
        if (remainder < 10) {
            return (char) ('0' + remainder);  // '0'부터 '9'까지
        } else {
            return (char) ('A' + (remainder - 10));  // 'A'부터 'P'까지
        }
    }

    // 26진수를 10진수로 변환 (0-9, A-P까지)
    public static int toDecimal(String base26) {
        if (base26 == null || base26.isEmpty()) {
            throw new IllegalArgumentException("입력값이 비어 있거나 null입니다.");
        }

        int result = 0;
        for (int i = 0; i < base26.length(); i++) {
            char c = base26.charAt(i);
            result = result * 26 + convertToDecimalValue(c);
        }
        return result;
    }

    // 26진수 문자를 10진수 값으로 변환
    private static int convertToDecimalValue(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';  // '0'은 0, '1'은 1, ..., '9'는 9
        } else if (c >= 'A' && c <= 'P') {
            return c - 'A' + 10;  // 'A'는 10, 'B'는 11, ..., 'P'는 25
        } else {
            throw new IllegalArgumentException("유효하지 않은 문자입니다. 0-9, A-P만 가능합니다: " + c);
        }
    }

    public static void main(String[] args) {
        try {
            // 테스트: 10진수 -> 26진수
            int number = 456975;
            String base26 = toBase26(number);
            System.out.println("10진수 " + number + "의 26진수 표현: " + base26);

            // 테스트: 26진수 -> 10진수
            base26 = "PPPP";
            int decimal = toDecimal(base26);
            System.out.println("26진수 " + base26 + "의 10진수 표현: " + decimal);

            // 추가 테스트: 0 처리
            System.out.println("10진수 0의 26진수 표현: " + toBase26(0));

        } catch (IllegalArgumentException e) {
            System.err.println("오류: " + e.getMessage());
        }
    }
}
