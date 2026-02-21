package Encoding;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class EncodingExample {
    public static void main(String[] args) {
        String origStr = "1─│┌┐┘└├┬┤┴┼가힝똠1샾♥♣☎0-1A-Za-z"; // 원본 문자열

        // UTF-8로 인코딩하여 출력
        byte[] utf8Bytes = origStr.getBytes(StandardCharsets.UTF_8);
        System.out.println("UTF8   출력: " + new String(utf8Bytes, StandardCharsets.UTF_8));

        // UTF-8을 MS949로 변환
        byte[] ms949Bytes = new String(utf8Bytes, StandardCharsets.UTF_8).getBytes(Charset.forName("MS949"));
        System.out.println("MS949  출력: " + new String(ms949Bytes, Charset.forName("MS949")));
 
        // MS949를 다시 UTF-8로 변환
        byte[] utf8FromMs949Bytes = new String(ms949Bytes, Charset.forName("MS949")).getBytes(StandardCharsets.UTF_8);
        System.out.println("UTF8   출력: " + new String(utf8FromMs949Bytes, StandardCharsets.UTF_8));
    }
}
