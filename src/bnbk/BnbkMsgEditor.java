package bnbk;

import java.util.List;
import java.util.Map;

import bnbk.PrinterProtocol.Command;
import bnbk.PrinterProtocol.PacketBuilder;

/**
 * 거래 목록을 통장기장 메시지로 바꾸는 편집기.
 *
 * C 원본 흐름 (edt_BnBkMsg.c):
 *   for (i = 0; i < li_DataCnt; i++) {
 *       fn_MkBnbkStr(plc_Char, field, EltLen[n], EltAttr[n]) × 12 필드
 *       if (li_CurPageCnt == 24) plc_Char += 4;  // auto turn
 *       LF(plc_Char, 1);
 *       li_CurPageCnt++;
 *   }
 */
public class BnbkMsgEditor {

    private static final int FIELD_COUNT = 12;

    /** 필드별 고정 바이트 길이 — EltLen[] 인덱스 순서 */
    private static final int[] ELT_LEN = {
         8,  // [0]  년월일
         3,  // [1]  지급통화코드
        13,  // [2]  출금액
        20,  // [3]  출금내용
        20,  // [4]  입금내용
         3,  // [5]  입금통화코드
        13,  // [6]  입금액
        13,  // [7]  잔액
        20,  // [8]  거래내용
        10,  // [9]  거래사무소코드
        20,  // [10] 등기번호
         1,  // [11] Skip여부
    };

    /** 출력 순서는 C 원본 구조체의 필드 순서를 그대로 따른다. */
    private static final String[] FIELD_KEYS = {
        "trDt",
        "pyCurc",
        "drwAm",
        "ioBbWbokDrwCntn",
        "ioBbWbokRvCntn",
        "rvCurc",
        "rvAm",
        "trafBac",
        "bbprtCntn",
        "trTrtBrc",
        "ioTgrmDataCntn120",
        "ioStopFlag"
    };

    /**
     * 거래 목록을 받아 통장기장 메시지를 만든다.
     *
     * @param trxList 기장 데이터 목록. 최대 76건까지 담을 수 있다.
     * @param funcSect 기능 구분 코드
     */
    public PrtBnbkMsg buildBnbkMsg(List<Map<String, String>> trxList, String funcSect) {
        System.out.println("[TRACE] buildBnbkMsg 시작: funcSect=" + funcSect + ", 입력 건수=" + trxList.size());

        PrtBnbkMsg msg = new PrtBnbkMsg();
        msg.setFuncSect(funcSect);

        int curPageCnt  = 0;
        int skipLineCnt = 0;
        int rowIndex = 0;

        for (Map<String, String> trx : trxList) {
            System.out.println("[TRACE] 거래 " + rowIndex + " 처리 시작");

            // Skip 행은 실제 출력 데이터에 넣지 않는다.
            if (BnbkTrxData.isSkip(trx)) {
                System.out.println("[TRACE] 거래 " + rowIndex + " 은(는) Skip 처리됨");
                skipLineCnt++;
                rowIndex++;
                continue;
            }

            PacketBuilder line = new PacketBuilder();

            // 24줄을 채우면 자동 줄넘김 명령을 먼저 넣는다.
            if (curPageCnt == PrtBnbkMsg.AUTO_TURN_AT) {
                System.out.println("[TRACE] 자동 줄넘김 명령 추가");
                line.appendAutoTurn();
                curPageCnt = 0;
            }

            appendTransactionFields(line, trx);

            // 한 거래의 끝마다 줄바꿈 명령을 붙인다.
            line.appendLineFeed(1);

            msg.addLine(line.build());
            curPageCnt++;
            System.out.println("[TRACE] 거래 " + rowIndex + " 처리 완료: 현재 출력 라인 수=" + msg.getDataCnt());
            rowIndex++;
        }

        msg.setSkipLineCnt(skipLineCnt);
        System.out.println("[TRACE] buildBnbkMsg 종료: 출력 라인 수=" + msg.getDataCnt() + ", skip 수=" + skipLineCnt);
        return msg;
    }

    /**
     * 거래 Map에서 12개 필드를 꺼내 고정 길이 바이트로 기록한다.
     */
    private void appendTransactionFields(PacketBuilder line, Map<String, String> trx) {
        for (int i = 0; i < FIELD_COUNT; i++) {
            String value = BnbkTrxData.getOrEmpty(trx, FIELD_KEYS[i]);
            line.appendField(value, ELT_LEN[i]);
        }
    }

    /**
     * 메시지 본문 앞뒤에 시작/종료 명령을 붙여 최종 패킷을 만든다.
     */
    public byte[] buildPacket(PrtBnbkMsg msg) {
        System.out.println("[TRACE] buildPacket 시작: 본문 라인 수=" + msg.getBnbkLines().size());
        PacketBuilder pb = new PacketBuilder();
        pb.append(Command.START_BNBK);
        for (byte[] line : msg.getBnbkLines()) {
            pb.write(line);
        }
        pb.append(Command.END_BNBK);
        System.out.println("[TRACE] buildPacket 종료");
        return pb.build();
    }
}
