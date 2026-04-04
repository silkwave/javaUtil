package consts;

import java.util.HashMap;
import java.util.Map;

/**
 * [최종형] 매체 거래 구분 코드 산출 엔진
 * 복잡한 IF-ELSE를 제거하고 Map 기반의 Rule Mapping 방식을 채택하여
 * 성능과 가독성, 유지보수성을 모두 잡은 구조입니다.
 */
public class MediaRuleEngine {

    // ==========================================
    // 1. 입력 매체 정의 (Enum)
    // ==========================================
    enum Channel {
        PB, MU, VM, SP, SA, QR, OTHER;

        /** 문자열로부터 안전하게 Enum을 추출합니다. */
        public static Channel from(String value) {
            if (value == null)
                return OTHER;
            try {
                return Channel.valueOf(value.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return OTHER;
            }
        }

        /** 비대면 채널 여부를 판별합니다. */
        public boolean isNonFace() {
            return this == MU || this == VM || this == SP || this == SA || this == QR;
        }
    }

    // ==========================================
    // 2. 룰 데이터 객체 (Rule Container)
    // ==========================================
    static class Rule {
        private final int pb; // 창구(PB)일 때의 코드
        private final int nonFace; // 비대면일 때의 코드
        private final int other; // 그 외(전자통장 등) 코드

        public Rule(int pb, int nonFace, int other) {
            this.pb = pb;
            this.nonFace = nonFace;
            this.other = other;
        }

        /** 채널 유형에 따른 최종 매체코드를 반환합니다. */
        public int resolve(Channel ch) {
            if (ch == Channel.PB)
                return pb;
            if (ch.isNonFace())
                return nonFace;
            return other;
        }
    }

    // ==========================================
    // 3. 룰 매핑 테이블 (Rule Registry)
    // ==========================================
    private static final Map<String, Rule> RULE_MAP = new HashMap<>();

    static {
        // [그룹 1] 요구불 계열: (창구:10, 비대면:21, 기타:15)
        register(
                "1,2,6,12,17,301,302,306,312,317,501,502,506,512,517," +
                        "51,52,55,56,351,352,355,356,551,552,555,556",
                new Rule(10, 21, 15));

        // [그룹 2] 저축성 계열: (창구:12, 비대면:23, 기타:17)
        register(
                "04,10,14,21,24,34,47,59,80,304,310,314,321,324,334,347,359,380," +
                        "54,60,84,94,98,354,360,384,394,398",
                new Rule(12, 23, 17));

        // [그룹 3] 신탁 계열: (창구:11, 비대면:22, 기타:16)
        register(
                "31,38,43,46,79,81,35,36,86,87,88,93",
                new Rule(11, 22, 16));

        // [특수 케이스] 과목코드 28: (창구:10, 비대면:24, 기타:18)
        RULE_MAP.put("28", new Rule(10, 24, 18));
    }

    /** CSV 형식의 과목코드들을 하나의 룰 객체에 등록합니다. */
    private static void register(String csv, Rule rule) {
        for (String code : csv.split(",")) {
            RULE_MAP.put(code.trim(), rule);
        }
    }

    // ==========================================
    // 4. 핵심 비즈니스 메서드
    // ==========================================
    public int getMediaCode(String subjectCode, String inputMedia) {
        if (subjectCode == null)
            return -1;

        String code = subjectCode.trim();
        Channel ch = Channel.from(inputMedia);

        ch.toString(); // 디버깅용 출력

        // Map에서 룰 검색 (O(1) 성능)
        Rule rule = RULE_MAP.get(code);

        // 등록되지 않은 과목코드인 경우
        if (rule == null)
            return -1;

        return rule.resolve(ch);
    }

    // ==========================================
    // 5. 테스트 메인
    // ==========================================
    public static void main(String[] args) {
        MediaRuleEngine engine = new MediaRuleEngine();

        System.out.println("========== [전체 룰 매핑 테이블] ==========");
        RULE_MAP.forEach((code, rule) -> {
            System.out.printf("과목코드: %-5s | 창구: %2d | 비대면: %2d | 기타: %2d%n",
                    code, rule.pb, rule.nonFace, rule.other);
        });

        System.out.println("========== [매체코드 산출 테스트] ==========");

        // 요구불 테스트
        test(engine, "1", "PB"); // 결과: 10
        test(engine, "356", "MU"); // 결과: 21

        // 저축성 테스트
        test(engine, "10", "PB"); // 결과: 12
        test(engine, "324", "VM"); // 결과: 23

        // 신탁 및 특수 케이스
        test(engine, "31", "QR"); // 결과: 22
        test(engine, "28", "xx"); // 결과: 10

        System.out.println("===========================================");
    }

    private static void test(MediaRuleEngine engine, String code, String media) {
        int result = engine.getMediaCode(code, media);
        System.out.printf("[검증] 과목:%-4s | 매체:%-7s | 코드:%d%n", code, media, result);
    }
}