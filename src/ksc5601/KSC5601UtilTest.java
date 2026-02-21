package ksc5601;

public class KSC5601UtilTest {
    public static void main(String[] args) {

        String input = "가힝똠1샾♥♣☎0-1A-Za-z"; // 입력 문자열
        int maxByteLen = 40; // 원하는 최대 바이트 길이 설정

        System.out.printf("입력 문자열: [%s]%n", input);
        KSC5601Util.printHex(input);
        int lengthInMS949 = KSC5601Util.getLengthInMS949(input);
        System.out.printf("입력 문자열 길이: [%s]%n", lengthInMS949);


        // 전각 문자 변환, KSC5601 필터링, 패딩을 적용하는 메서드 호출
        String result = KSC5601Util.convertToFullWidthFilterAndPad(input, maxByteLen);
        System.out.printf("처리된 문자열: [%s]%n", result);
        KSC5601Util.printHex(result);

        lengthInMS949 = KSC5601Util.getLengthInMS949(result);
        System.out.printf("처리된 문자열 길이: [%s]%n", lengthInMS949);


        // 반각 변환 및 rtrim 적용
        String halfWidthResult = KSC5601Util.convertToHalfWidthAndTrimRight(result);
        System.out.printf("반각 변환 문자열: [%s]%n", halfWidthResult);
        KSC5601Util.printHex(halfWidthResult);

    }
}
