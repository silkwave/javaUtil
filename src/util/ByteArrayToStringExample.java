package util;

import java.util.Arrays;

public class ByteArrayToStringExample {
    public static void main(String[] args) {
        // byte 배열 초기화 (유효하지 않은 바이트 0xFF 포함)
        byte[] byteArray = { 72, 101, 108, 108, 111, (byte) 0x31, (byte) 0x42 };

        // 새로 추가된 유틸리티 메서드를 사용하여 필터링과 문자열 변환을 한 번에 수행합니다.
        String str = Util.toSafeString(byteArray, "MS949");

        // 결과 출력
        System.out.println("Original byte array: " + Arrays.toString(byteArray));
        System.out.println("Converted String: " + str); // 예상 출력: HelloI

        char stx = 0x31; // 문자 '1'의 코드 포인트
        char etx = 0x42; // 문자 'B'의 코드 포인트

        char bt  = (char) byteArray[1]; // byte를 char로 형 변환


        // char를 String으로 명시적으로 변환하여 결합합니다.
        String testStr = String.valueOf(stx) + "Hello" + String.valueOf(etx) + String.valueOf(bt);
        System.out.println("Test String with control chars: " + testStr); // 예상 출력: 1HelloBe

        // Util 클래스의 byte -> String 변환 메서드 호출 및 결과 출력
        String convertedStringFromByte = Util.byteToCharString(byteArray[0]);
        System.out.println("\n--- byte -> String 변환 결과 ---");
        System.out.println("byte " + byteArray[0] + " -> String: \"" + convertedStringFromByte + "\"");

    }
}
