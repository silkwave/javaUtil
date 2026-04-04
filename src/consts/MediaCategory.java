package consts;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 미디어 코드를 상위 카테고리로 변환하는 enum
 */
public enum MediaCategory {

    /** PB 계열 (수수료율 10) */
    PB(10),

    /** 일반 미디어 (수수료율 21) */
    NON_PB(21),

    /** 기타/미정 (수수료율 15) */
    ETC(15);

    /** 카테고리별 수수료율 */
    private final int feeRate;

    MediaCategory(int feeRate) {
        this.feeRate = feeRate;
    }

    /** 수수료율 조회 */
    public int getFeeRate() {
        return feeRate;
    }

    /** 미디어 코드 → 카테고리 매핑 */
    private static final Map<String, MediaCategory> CATEGORY_BY_CODE;

    static {
        Map<String, MediaCategory> map = new HashMap<>();
        map.put("PB", PB);
        map.put("MU", NON_PB);
        map.put("VM", NON_PB);
        map.put("SP", NON_PB);
        map.put("SA", NON_PB);
        map.put("QR", NON_PB);
        CATEGORY_BY_CODE = Collections.unmodifiableMap(map);
    }

    /**
     * 코드 → 카테고리 변환
     * - null → ETC
     * - trim + 대문자 변환
     * - 없으면 ETC
     */
    public static MediaCategory from(String code) {
        if (code == null) {
            return ETC;
        }
        return CATEGORY_BY_CODE.getOrDefault(code.trim().toUpperCase(), ETC);
    }

    public static void main(String[] args) {
        String[] sampleCodes = { "PB", "MU", "VM", "SP", "SA", "QR", "ETC", "unknown", null };

        System.out.println("media        | category | feeRate");
        System.out.println("-------------|----------|--------");

        for (String code : sampleCodes) {
            MediaCategory category = MediaCategory.from(code);
            int feeRate = category.getFeeRate();

            System.out.printf("%-12s | %-8s | %d%n", code, category, feeRate);
        }
    }
}