package json;

import java.util.*;

public class MapCleaner {

    /**
     * 주어진 Map<String, Object>를 재귀적으로 탐색하여 다음 값을 제거합니다:
     * - null 값
     * - 빈 문자열 ("")
     * - "0"이라는 문자열
     * - 숫자 0 (Integer, Long, Double 등)
     * - 빈 Map (사이즈 0)
     * - 빈 List (사이즈 0)
     *
     * 이 과정에서 Map 내부에 중첩된 Map이나 List도 동일하게 재귀적으로 정제합니다.
     *
     * @param inputMap 정제 대상 Map
     * @return 정제 후 값이 남아있는 Map 반환 (전부 제거되면 빈 Map)
     */
    public static Map<String, Object> removeMapNullValues(Map<String, Object> inputMap) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 입력 Map의 각 키-값 쌍에 대해 처리
        inputMap.forEach((key, value) -> {
            if (value instanceof Map<?, ?> mapVal) {
                // 값이 Map 타입이면 재귀 정제 후, 비어있지 않으면 결과에 포함
                Map<String, Object> cleaned = convertAndCleanMap(mapVal);
                if (!cleaned.isEmpty()) result.put(key, cleaned);

            } else if (value instanceof List<?> listVal) {
                // 값이 List 타입이면 재귀 정제 후, 비어있지 않으면 결과에 포함
                List<Object> cleaned = cleanList(listVal);
                if (cleaned != null && !cleaned.isEmpty()) result.put(key, cleaned);

            } else if (!shouldRemove(key, value)) {
                // 일반 값은 제거 조건에 해당하지 않을 때만 결과에 포함
                result.put(key, value);
            }
        });

        return result;
    }

    /**
     * 주어진 리스트를 재귀적으로 탐색하여 다음 항목들을 제거합니다:
     * - null 값
     * - 빈 문자열 ("")
     * - "0" 문자열
     * - 숫자 0
     * - 빈 Map
     * - 빈 List
     *
     * 리스트 안에 Map 또는 List가 중첩되어 있으면 재귀 정제합니다.
     *
     * @param list 정제 대상 리스트
     * @return 정제 후 리스트 반환, 모든 요소가 제거되면 null 반환
     */
    private static List<Object> cleanList(List<?> list) {
        List<Object> result = new ArrayList<>();

        for (Object item : list) {
            if (shouldRemove(null, item)) continue;

            if (item instanceof Map<?, ?> mapVal) {
                Map<String, Object> cleaned = convertAndCleanMap(mapVal);
                if (!cleaned.isEmpty()) result.add(cleaned);

            } else if (item instanceof List<?> listVal) {
                List<Object> cleaned = cleanList(listVal);
                if (cleaned != null && !cleaned.isEmpty()) result.add(cleaned);

            } else {
                result.add(item);
            }
        }

        return result.isEmpty() ? null : result;
    }

    /**
     * 주어진 값이 제거 조건에 해당하는지 검사합니다.
     * 
     * 제거 조건:
     * - null 값
     * - 빈 문자열 ("") 또는 공백만 있는 문자열
     * - 문자열 "0"
     * - 숫자 0 (정수/실수 모두 포함)
     * - 빈 Map (크기 0)
     * - 빈 List (크기 0)
     *
     * @param key   현재 키 이름 (필요 시 사용, 현재는 미사용)
     * @param value 검사할 값
     * @return 제거 대상이면 true, 아니면 false
     */
    private static boolean shouldRemove(String key, Object value) {
        if (value == null) return true;

        if (value instanceof String s) {
            String str = s.trim();
            return str.isEmpty() || str.equals("0");
        }

        if (value instanceof Number n) {
            return n.intValue() == 0;
        }

        if (value instanceof Map<?, ?> m) {
            return m.isEmpty();
        }

        if (value instanceof List<?> l) {
            return l.isEmpty();
        }

        return false;
    }

    /**
     * Map<?, ?> 타입을 안전하게 Map<String, Object>로 변환하고,
     * 재귀적으로 정제 작업을 수행합니다.
     *
     * @param rawMap 원본 Map (키 타입이 확실하지 않음)
     * @return 정제된 Map<String, Object>
     */
    private static Map<String, Object> convertAndCleanMap(Map<?, ?> rawMap) {
        Map<String, Object> castedMap = new LinkedHashMap<>();
        rawMap.forEach((k, v) -> {
            if (k instanceof String key) castedMap.put(key, v);
        });
        return removeMapNullValues(castedMap);
    }

    // 간단한 테스트용 main 메서드
    public static void main(String[] args) {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("name", "test");
        input.put("zero_cnt", "0000");

        Map<String, Object> childList3 = new LinkedHashMap<>();
        childList3.put("child_name1", "111");
        childList3.put("child_name2", "1111");

        Map<String, Object> childList2 = new LinkedHashMap<>();
        childList2.put("childList3", childList3);

        Map<String, Object> childList1 = new LinkedHashMap<>();
        childList1.put("childList2", childList2);

        Map<String, Object> childMap = new LinkedHashMap<>();
        childMap.put("child_cnt", "00000");
        childMap.put("childList1", childList1);

        Map<String, Object> nestedMap = new LinkedHashMap<>();
        nestedMap.put("innerCount", 0);
        nestedMap.put("innerData", "value");
        nestedMap.put("childMap", childMap);

        input.put("nestedMap", nestedMap);

        Map<String, Object> cleaned = removeMapNullValues(input);

        System.out.println(new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(cleaned));
    }
}
