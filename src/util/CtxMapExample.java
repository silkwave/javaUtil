package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

/**
 * CtxMap 사용 예제 및 데모용 main
 */
public class CtxMapExample {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        CtxMap ctx = new CtxMap();

        initContext(ctx);
        printContext(ctx, "📘 초기 컨텍스트:");

        updateContext(ctx);
        printContext(ctx, "\n📗 업데이트된 컨텍스트:");

        PersonDto person = createPerson();
        ctx.put("person", person);
        ctx.put("personJson", gson.toJson(person));

        printJson(ctx);
        deserializePerson(ctx);

        // 7️⃣ Optional 기반 접근 예제
        demoOptional(ctx);

        printContextAsJson(ctx);
        deserializeContext(ctx);
    }

    private static void initContext(CtxMap ctx) {
        ctx.put("numbers", Arrays.asList(1, 2, 3));
        ctx.put("message", "Hello");
        ctx.put("count", 123);
    }

    private static void updateContext(CtxMap ctx) {
        ctx.put("isActive", true);
        ctx.put("fruits", Arrays.asList("Apple", "Banana", "Cherry"));
    }

    private static PersonDto createPerson() {
        return new PersonDto("홍길동", 30, Arrays.asList("독서", "등산", "코딩"));
    }

    private static void printContext(CtxMap ctx, String title) {
        System.out.println(title);
        ctx.asReadOnlyMap().forEach((k, v) -> System.out.println(" - " + k + " : " + v));
    }

    private static void printJson(CtxMap ctx) {
        System.out.println("\n📗 JSON 문자열 (Pretty):");
        String json = ctx.getObject("personJson", String.class);
        System.out.println(json);
    }

    private static void deserializePerson(CtxMap ctx) {
        System.out.println("\n📘 JSON → PersonDto 변환 결과:");
        String json = ctx.getObject("personJson", String.class);
        if (json == null) {
            System.out.println("personJson not present or not a String.");
            return;
        }
        PersonDto person = gson.fromJson(json, PersonDto.class);
        System.out.println(person);
    }

    private static void demoOptional(CtxMap ctx) {
        System.out.println("\n💡 Optional 기반 접근 예제:");

        ctx.getOptional("personJson", String.class)
                .map(json -> gson.fromJson(json, PersonDto.class))
                .ifPresentOrElse(
                        p -> System.out.println(" - person via Optional.map(): " + p),
                        () -> System.out.println(" - personJson absent")
                );

        ctx.getOptional("person", PersonDto.class)
                .ifPresent(p -> System.out.println(" - person present via getOptional(): " + p));

        String missing = ctx.getOptional("missingKey", String.class).orElse("defaultValue");
        System.out.println(" - missingKey orElse: " + missing);
    }

    private static void printContextAsJson(CtxMap ctx) {
        System.out.println("\n📙 전체 Context를 JSON으로 변환:");
        String json = gson.toJson(ctx.asReadOnlyMap());
        System.out.println(json);
    }

    private static void deserializeContext(CtxMap ctx) {
        System.out.println("\n📗 JSON → Map<String, Object> 복원:");
        String json = gson.toJson(ctx.asReadOnlyMap());
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> restored = gson.fromJson(json, type);
        restored.forEach((k, v) -> System.out.println(" - " + k + " : " + v));
    }
}