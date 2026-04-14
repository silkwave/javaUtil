package bnbk;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 통장기장 1행 데이터를 Map으로 다룰 때 쓰는 도우미.
 *
 * 지금 구조에서는 별도의 VO 객체 대신
 * {@code Map<String, String>} 하나가 거래 1건을 의미한다.
 * 이 클래스는 그때 필요한 키 이름과 공통 처리 로직만 모아 둔다.
 */
public final class BnbkTrxData {

    private BnbkTrxData() {
    }

    /**
     * 거래 1건을 보기 좋은 순서로 만든다.
     *
     * LinkedHashMap을 쓰는 이유는 값을 넣은 순서가 유지되어
     * 디버깅하거나 로그를 볼 때 읽기 쉽기 때문이다.
     */
    public static Map<String, String> createRow(Map<String, String> source) {
        Map<String, String> row = new LinkedHashMap<String, String>();
        row.put("trDt", getOrEmpty(source, "trDt"));
        row.put("pyCurc", getOrEmpty(source, "pyCurc"));
        row.put("drwAm", getOrEmpty(source, "drwAm"));
        row.put("ioBbWbokDrwCntn", getOrEmpty(source, "ioBbWbokDrwCntn"));
        row.put("ioBbWbokRvCntn", getOrEmpty(source, "ioBbWbokRvCntn"));
        row.put("rvCurc", getOrEmpty(source, "rvCurc"));
        row.put("rvAm", getOrEmpty(source, "rvAm"));
        row.put("trafBac", getOrEmpty(source, "trafBac"));
        row.put("bbprtCntn", getOrEmpty(source, "bbprtCntn"));
        row.put("trTrtBrc", getOrEmpty(source, "trTrtBrc"));
        row.put("ioTgrmDataCntn120", getOrEmpty(source, "ioTgrmDataCntn120"));
        row.put("ioStopFlag", getOrEmpty(source, "ioStopFlag"));
        return row;
    }

    /**
     * Map에서 값을 읽되, 값이 없으면 빈 문자열을 돌려준다.
     *
     * 직렬화 단계에서는 null보다 빈 문자열이 다루기 쉽다.
     */
    public static String getOrEmpty(Map<String, String> trx, String key) {
        if (trx == null) {
            return "";
        }
        String value = trx.get(key);
        return value != null ? value : "";
    }

    /**
     * Skip 플래그가 Y이면 출력 대상에서 제외할 행으로 본다.
     */
    public static boolean isSkip(Map<String, String> trx) {
        return "Y".equalsIgnoreCase(getOrEmpty(trx, "ioStopFlag"));
    }
}
