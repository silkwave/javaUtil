package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MapUtilsDemo: `MapUtils.toMap(...)` 사용 예제를 담은 데모 클래스
 * <p>
 * - DTO, Map, CtxMap 등 다양한 입력을 안전하게 Map<String, Object>로 변환
 * - 변환 결과를 pretty JSON과 키별 타입 정보로 출력
 */
public class MapUtilsDemo {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        // DTO -> Map
        PersonDto person = new PersonDto("박영희", 28, java.util.Arrays.asList("음악", "요리"));
        printlnSection("📘 DTO -> Map 예제 (PersonDto):");
        System.out.println("PersonDto: " + person);

        Map<String, Object> personMap = MapUtils.toMap(person);
        printMapDetails(personMap);

        // Map -> Map (이미 Map인 경우에도 안전하게 처리)
        var nested = new LinkedHashMap<String, Object>();
        nested.put("inner", Map.of("x", 1, "y", 2));
        nested.put("label", "sample");

        printlnSection("📗 Map -> Map 예제 (LinkedHashMap):");
        System.out.println("Original map: " + nested);
        Map<String, Object> converted = MapUtils.toMap(nested);
        printMapDetails(converted);

        // CtxMap 내부 맵 변환 예제
        CtxMap ctx = new CtxMap();
        ctx.put("person", person);
        ctx.put("count", 5);

        printlnSection("📙 CtxMap.asReadOnlyMap() -> Map 변환 예제:");
        Map<String, Object> fromCtx = MapUtils.toMap(ctx.asReadOnlyMap());
        printMapDetails(fromCtx);

        // JSON 문자열로 직렬화된 값도 Object로 주입되면 변환 가능
        ctx.put("personJson", gson.toJson(person));
        printlnSection("💡 JSON 문자열을 가진 Map을 변환해도 내부 값은 문자열로 유지됩니다:");
        Map<String, Object> fromCtx2 = MapUtils.toMap(ctx.asReadOnlyMap());
        printMapDetails(fromCtx2);

        printlnSection("✅ 데모 완료");
    }

    private static void printlnSection(String title) {
        System.out.println("\n--- " + title + " ---");
    }

    private static void printMapDetails(Map<String, Object> map) {
        if (map == null) {
            System.out.println(" (null)\n");
            return;
        }

        // pretty JSON 출력
        try {
            System.out.println("Pretty JSON:\n" + gson.toJson(map));
        } catch (Exception ignored) {
            System.out.println("Converted Map: " + map);
        }

        System.out.println(" - 상세 항목 (키, 값, 값의 타입):");
        map.forEach((k, v) -> System.out.printf("   * %s = %s (%s)%n", k, v, (v == null ? "null" : v.getClass().getSimpleName())));
    }
}