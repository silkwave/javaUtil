package consts;

import java.util.HashMap;
import java.util.Map;

/**
 * [최종형] 매체 거래 구분 코드 산출 엔진
 */
public class MediaRuleEngine {

    // ==========================================
    // 1. 입력 매체 정의 (Enum)
    // ==========================================
    public enum Channel {
        PB, NON_FACE, OTHER;

        public static Channel from(String value) {
            return switch (value) {
                case "PB" -> PB;
                case "APP", "WEB", "MOBILE", "MU", "VM", "QR" -> NON_FACE;
                default -> OTHER;
            };
        }
    }

    // ==========================================
    // 2. Rule → class 유지 (핵심)
    // ==========================================
    static class Rule {

        private final int pb;
        private final int nonFace;
        private final int other;

        public Rule(int pb, int nonFace, int other) {
            this.pb = pb;
            this.nonFace = nonFace;
            this.other = other;
        }

        public int resolve(Channel ch) {
            return switch (ch) {
                case PB -> pb;
                case NON_FACE -> nonFace;
                case OTHER -> other;
            };
        }

        @Override
        public String toString() {
            return String.format("[PB:%d, NON_FACE:%d, OTHER:%d]", pb, nonFace, other);
        }
    }

    // ==========================================
    // 3. 룰 매핑 테이블
    // ==========================================
    private static final Map<String, Rule> RULE_MAP = new HashMap<>();

    static {
        // 요구불
        register(
                "1,2,6,12,17,301,302,306,312,317,501,502,506,512,517," +
                "51,52,55,56,351,352,355,356,551,552,555,556",
                new Rule(10, 21, 15));

        // 저축성
        register(
                "04,10,14,21,24,34,47,59,80,304,310,314,321,324,334,347,359,380," +
                "54,60,84,94,98,354,360,384,394,398",
                new Rule(12, 23, 17));

        // 신탁
        register(
                "31,38,43,46,79,81,35,36,86,87,88,93",
                new Rule(11, 22, 16));

        // 특수
        RULE_MAP.put("28", new Rule(10, 24, 18));
    }

    private static void register(String csv, Rule rule) {
        for (String code : csv.split(",")) {
            RULE_MAP.put(code.trim(), rule);
        }
    }

    // ==========================================
    // 4. 핵심 메서드
    // ==========================================
    public int getMediaCode(String subjectCode, String inputMedia) {

        if (subjectCode == null) return -1;

        Channel ch = Channel.from(inputMedia);
        Rule rule = RULE_MAP.get(subjectCode.trim());

        if (rule == null) return -1;

        return rule.resolve(ch);
    }

    // ==========================================
    // 5. 테스트
    // ==========================================
    public static void main(String[] args) {
        MediaRuleEngine engine = new MediaRuleEngine();

        System.out.println("========== [룰 테이블] ==========");
        RULE_MAP.forEach((code, rule) ->
                System.out.printf("%-5s -> %s%n", code, rule)
        );

        System.out.println("========== [테스트] ==========");

        test(engine, "1", "PB");
        test(engine, "356", "MU");
        test(engine, "10", "PB");
        test(engine, "324", "VM");
        test(engine, "31", "QR");
        test(engine, "28", "xx");
    }

    private static void test(MediaRuleEngine engine, String code, String media) {
        int result = engine.getMediaCode(code, media);
        System.out.printf("[검증] 과목:%-4s | 매체:%-7s | 코드:%d%n", code, media, result);
    }
}