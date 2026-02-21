package Encoding;

import java.nio.charset.Charset;
import java.util.Arrays;

public class byteCut {
    public static void main(String[] args) {
        String origStr = "가힝똠1샾♥♣☎0-1A-Za-z";
        
        String ms949Sub = getSubByBytes(origStr, 4, 4 + 5, "MS949");
        System.out.println(ms949Sub);
        
        ms949Sub = getSubByBytes(origStr, 9, 9 + 2, "MS949");
        System.out.println(ms949Sub);
    }

    private static String getSubByBytes(String inputStr, int startIdx, int endIdx, String enc) {
        Charset cs = Charset.forName(enc);
        byte[] strBytes = inputStr.getBytes(cs);
        printHex(strBytes);
        
        int start = Math.max(0, Math.min(startIdx, strBytes.length));
        int end = Math.max(start, Math.min(endIdx, strBytes.length));
        
        byte[] byteSub = Arrays.copyOfRange(strBytes, start, end);
        printHex(byteSub);
        
        return new String(byteSub, cs);
    }

    private static void printHex(byte[] byteArr) {
        for (byte b : byteArr) {
            System.out.print(String.format("0x%02X ", b));
        }
        System.out.println();
    }
}
