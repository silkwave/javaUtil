package seed;

import java.nio.charset.Charset;

public class SeedCryptoExample {

    public static void main(String[] args) throws Exception {

        byte[] key = "1234567890123456".getBytes();   // PG에서 받은 키
        byte[] iv  = "1234567890123456".getBytes();   // PG에서 명시한 IV

        String plain =
                "ORDERID=20251223" +
                "&AMT=10000" +
                "&MID=PGMID001";

        // 대부분 PG는 EUC-KR
        Charset cs = Charset.forName("EUC-KR");

        String encHex = SeedCryptoUtil.encryptHex(key, iv, plain, cs);
        System.out.println("ENC HEX : " + encHex);

        String dec = SeedCryptoUtil.decryptHex(key, iv, encHex, cs);
        System.out.println("DEC     : " + dec);
    }
}
