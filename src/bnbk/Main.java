package bnbk;



import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import bnbk.PrinterProtocol.PacketBuilder;

public class Main {
    public static void main(String[] args) {
        System.out.println("[TRACE] 프로그램 시작");

        // 1. 예제 거래 데이터를 준비한다.
        List<Map<String, String>> transactions = sampleTransactions();
        System.out.println("[TRACE] 샘플 거래 데이터 준비 완료: " + transactions.size() + "건");

        // 2. 거래 데이터를 통장기장 메시지로 변환한다.
        PrtBnbkMsg message = buildMessage(transactions);
        System.out.println("[TRACE] 통장기장 메시지 생성 완료");

        System.out.println("[TRACE] 생성된 라인 내용을 확인한다.");
        message.getBnbkLines().forEach(line -> {
            String lineStr = new String(line, Charset.forName("EUC-KR")).trim();
            System.out.println("[TRACE] Line: " + lineStr);
        });

        // 3. 변환 결과를 화면에 출력해 확인한다.
        printMessageSummary(message);
        printPacket(message);
        printSerializedSize(message);
        System.out.println("[TRACE] 프로그램 종료");
    }

    /**
     * 예제를 실행할 때 사용할 샘플 거래 목록을 만든다.
     */
    private static List<Map<String, String>> sampleTransactions() {
        return List.of(
            BnbkTrxData.createRow(
                sourceRow(
                    "20260414",   // 년월일
                    "KRW",        // 지급통화코드
                    "500000",     // 출금액
                    "ATM출금",    // 출금내용
                    "",           // 입금내용
                    "",           // 입금통화코드
                    "",           // 입금액
                    "1234567",    // 잔액
                    "현금출금",   // 거래내용
                    "서울중앙",   // 거래사무소코드
                    "",           // 등기번호
                    "N"           // Skip여부
                )
            ),
            BnbkTrxData.createRow(
                sourceRow(
                    "20260414",
                    "KRW",
                    "",
                    "",
                    "200000",
                    "KRW",
                    "200000",
                    "1434567",
                    "급여이체",
                    "강남지점",
                    "",
                    "N"
                )
            ),
            BnbkTrxData.createRow(
                sourceRow(
                    "20260414", "", "", "", "", "", "", "", "", "", "", "Y"
                )
            )
        );
    }

    /**
     * 예제용 원본 Map을 간단하게 만든다.
     */
    private static Map<String, String> sourceRow(
        String trDt,
        String pyCurc,
        String drwAm,
        String ioBbWbokDrwCntn,
        String ioBbWbokRvCntn,
        String rvCurc,
        String rvAm,
        String trafBac,
        String bbprtCntn,
        String trTrtBrc,
        String ioTgrmDataCntn120,
        String ioStopFlag
    ) {
        return Map.ofEntries(
            Map.entry("trDt", trDt),
            Map.entry("pyCurc", pyCurc),
            Map.entry("drwAm", drwAm),
            Map.entry("ioBbWbokDrwCntn", ioBbWbokDrwCntn),
            Map.entry("ioBbWbokRvCntn", ioBbWbokRvCntn),
            Map.entry("rvCurc", rvCurc),
            Map.entry("rvAm", rvAm),
            Map.entry("trafBac", trafBac),
            Map.entry("bbprtCntn", bbprtCntn),
            Map.entry("trTrtBrc", trTrtBrc),
            Map.entry("ioTgrmDataCntn120", ioTgrmDataCntn120),
            Map.entry("ioStopFlag", ioStopFlag)
        );
    }

    /**
     * 거래 목록을 장비가 이해할 수 있는 통장기장 메시지로 바꾼다.
     */
    private static PrtBnbkMsg buildMessage(List<Map<String, String>> transactions) {
        System.out.println("[TRACE] buildMessage 호출: funcSect=BOOKPR, 입력 건수=" + transactions.size());
        BnbkMsgEditor editor = new BnbkMsgEditor();
        return editor.buildBnbkMsg(transactions, "BOOKPR");
    }

    /**
     * 메시지의 기본 정보를 출력한다.
     */
    private static void printMessageSummary(PrtBnbkMsg msg) {
        System.out.println("=== PrtBnbkMsg ===");
        System.out.println("FuncSect    : " + msg.getFuncSect());
        System.out.println("DataCnt     : " + msg.getDataCnt());
        System.out.println("SkipLineCnt : " + msg.getSkipLineCnt());
        System.out.println("Lines       : " + msg.getBnbkLines().size());
    }

    /**
     * 장비 전송용 패킷을 만들어 16진수 형태로 보여 준다.
     */
    private static void printPacket(PrtBnbkMsg msg) {
        BnbkMsgEditor editor = new BnbkMsgEditor();
        byte[] packet = editor.buildPacket(msg);
        System.out.println("[TRACE] 최종 패킷 생성 완료: " + packet.length + " bytes");
        System.out.printf("%n=== Final Packet (%d bytes) ===%n", packet.length);

        PacketBuilder dump = new PacketBuilder();
        dump.write(packet);
        System.out.print(dump.toHexDump());
    }

    /**
     * C 구조체와 같은 레이아웃으로 직렬화한 결과 크기를 보여 준다.
     */
    private static void printSerializedSize(PrtBnbkMsg msg) {
        byte[] serialized = msg.serialize(Charset.forName("EUC-KR"));
        System.out.println("[TRACE] 직렬화 완료: " + serialized.length + " bytes");
        System.out.printf("%n=== Serialized PRT_BNBK_MSG (%d bytes) ===%n", serialized.length);
        System.out.printf("예상 크기: 6 + 2 + 3 + 76*120 = %d bytes%n", 6 + 2 + 3 + 76 * 120);
    }
}
