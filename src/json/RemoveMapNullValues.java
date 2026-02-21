package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

public class RemoveMapNullValues {

    /**
     * Map<String, Object> 타입의 입력 데이터를 재귀적으로 순회하며 다음 조건에 해당하는 항목을 제거합니다.
     * - 값이 null인 경우
     * - 빈 문자열("") 또는 공백만 포함된 문자열
     * - 문자열 "0" (공백 제거 후)
     * - 숫자 0 (doubleValue 기준)
     * - _cnt로 끝나는 키의 값이 위 조건에 해당하는 경우
     * - 비어 있는 Map (내부 키가 모두 제거된 경우)
     * - 비어 있는 List, 빈 배열 (요소가 하나도 없는 경우)
     *
     * @param inputMap 정제 대상 Map
     * @return 정제된 Map (빈 항목 제거 후 반환, 모든 값 제거된 경우 null 반환 가능)
     */
    public static Map<String, Object> removeMapNullValues(Map<String, Object> inputMap) {
        Map<String, Object> result = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 1. null 제거
            if (value == null) continue;

            // 2. "_cnt"로 끝나는 키는 값이 "0", 0, 빈 문자열이면 제거
            if (key.endsWith("_cnt")) {
                if (value instanceof String s && (s.trim().isEmpty() || s.trim().equals("0"))) continue;
                if (value instanceof Number num && num.doubleValue() == 0.0) continue;
            }

            // 3. 빈 문자열 제거
            if (value instanceof String && ((String) value).trim().isEmpty()) continue;

            // 4. Map인 경우 재귀적으로 정제
            if (value instanceof Map<?, ?>) {
                Map<String, Object> cleaned = removeMapNullValues((Map<String, Object>) value);
                if (cleaned != null && !cleaned.isEmpty()) {
                    result.put(key, cleaned);
                }
                continue;
            }

            // 5. List인 경우 → 비어있지 않을 때만 추가
            if (value instanceof List<?>) {
                List<?> list = (List<?>) value;
                if (!list.isEmpty()) {
                    result.put(key, list);
                }
                continue;
            }

            // 6. 배열인 경우 → 문자열/Null 제거 후 비어있지 않으면 추가
            if (value.getClass().isArray()) {
                Object[] arr = (Object[]) value;
                List<Object> cleanedList = new ArrayList<>();
                for (Object item : arr) {
                    if (item instanceof String && ((String) item).trim().isEmpty()) continue;
                    if (item != null) cleanedList.add(item);
                }
                if (!cleanedList.isEmpty()) {
                    result.put(key, cleanedList);
                }
                continue;
            }

            // 7. 그 외 타입은 그대로 추가
            result.put(key, value);
        }

        return result.isEmpty() ? null : result;
    }

    // 테스트용 main 메서드
    public static void main(String[] args) {
        Map<String, Object> jsonMap = new LinkedHashMap<>();

        jsonMap.put("name", "John");
        jsonMap.put("emptyString", "");       // 빈 문자열 → 제거 대상
        jsonMap.put("nullField", null);       // null → 제거 대상
        jsonMap.put("age", 30);

        Map<String, Object> addressMap = new LinkedHashMap<>();
        addressMap.put("street", "");         // 빈 문자열 → 제거 대상
        addressMap.put("city", "Seoul");
        addressMap.put("zipcode", "");        // 빈 문자열 → 제거 대상
        jsonMap.put("address", addressMap);

        jsonMap.put("hobbies", new Object[] {});                                // 빈 배열 → 제거 대상
        jsonMap.put("skills", new Object[] { "Java", "", null });               // "Java"만 남고 나머지 제거

        Map<String, Object> socialMap = new LinkedHashMap<>();
        socialMap.put("twitter", "");      // 제거 대상
        socialMap.put("github", "");       // 제거 대상

        Map<String, Object> profileMap = new LinkedHashMap<>();
        profileMap.put("bio", "");         // 빈 문자열 → 제거 대상
        profileMap.put("aaa_cnt", "0");    // "_cnt" 조건 → 제거 대상
        profileMap.put("social", socialMap); // 내부도 모두 제거되면 함께 제거됨
        jsonMap.put("profile", profileMap);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        System.out.println("⛔ 원본 JSON:");
        System.out.println(gson.toJson(jsonMap));

        Map<String, Object> cleanedMap = removeMapNullValues(jsonMap);

        System.out.println("\n✅ 정제 후 결과 Map:");
        System.out.println(gson.toJson(cleanedMap));
    }
}
