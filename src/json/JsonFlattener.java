package json;

import com.google.gson.*;
import lombok.Data;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.Map;

/**
 * DTO (Data Transfer Object)
 * Lombok의 @Data를 사용하여 Getter, Setter, toString 등을 자동 생성합니다.
 */
@Data
class JsonDto {
    private String name;
    private String email;
    private String phoneNumber;
    private Integer age;
    private String address;
    private String role;
    // 이 필드에 JSON 문자열이 저장됩니다. 이 문자열이 최종 객체에 병합될 대상입니다.
    private String searchKey;
    private OffsetDateTime createdAt;
}

/**
 * OffsetDateTime 직렬화/역직렬화를 위한 Gson TypeAdapter
 * ISO 8601 문자열 포맷으로 처리합니다.
 */
class OffsetDateTimeAdapter implements JsonSerializer<OffsetDateTime>, JsonDeserializer<OffsetDateTime> {

    @Override
    public JsonElement serialize(OffsetDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        // OffsetDateTime 객체를 문자열로 변환하여 JsonPrimitive로 반환
        return new JsonPrimitive(src.toString());
    }

    @Override
    public OffsetDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // JSON 문자열을 OffsetDateTime 객체로 파싱하여 반환
        return OffsetDateTime.parse(json.getAsString());
    }
}

/**
 * 메인 클래스: DTO를 JSON으로 변환하고 searchKey 필드를 병합/제거하는 로직을 포함합니다.
 */
public class JsonFlattener {

    private static final Gson GSON;

    // Gson 인스턴스를 static 블록에서 초기화하여 재사용성을 높입니다.
    static {
        GSON = new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    public static void main(String[] args) {
        // DTO 초기화
        JsonDto dto = new JsonDto();
        dto.setName("Alice");
        dto.setEmail("alice@example.com");
        dto.setPhoneNumber("123-456-7890");
        dto.setAge(30);
        dto.setAddress("123 Main St");
        dto.setRole("USER");
        // 병합할 JSON 문자열을 searchKey에 저장
        dto.setSearchKey("{ \"searchName\":\"John\", \"searchAge\":31, \"searchCity\":\"New York\" }");
        dto.setCreatedAt(OffsetDateTime.parse("2024-06-01T12:00:00Z"));

        System.out.println("📦 원본 DTO를 JSON 객체로 변환 (searchKey는 아직 문자열):\n" + GSON.toJson(dto));

        // 핵심 로직 실행: JSON 객체 평탄화 (Flattening)
        String finalJson = processJsonFlattening(dto);

        System.out.println("\n✅ 최종 JSON 객체 (searchKey 내용 병합 및 제거):\n" + finalJson);
    }

    /**
     * DTO 객체를 JSON으로 변환한 후, searchKey 필드의 내용을 객체에 병합하고 원본 필드를 제거합니다.
     * * @param dto 처리할 DTO 객체
     * @return 병합 및 제거 처리가 완료된 JSON 문자열
     */
    public static String processJsonFlattening(JsonDto dto) {
        // 1. DTO를 JsonElement (JsonObject)로 변환
        // DTO -> JSON String -> JsonElement (JsonObject)
        JsonObject jsonObject = GSON.toJsonTree(dto).getAsJsonObject();

        // 2. searchKey 값 (JSON String) 추출 및 파싱
        String searchKeyString = dto.getSearchKey();
        
        // JsonParser.parseString을 사용하여 문자열을 JsonObject로 파싱합니다.
        // 이 부분은 try-catch로 JsonSyntaxException을 처리하는 것이 더 안전합니다.
        JsonObject searchKeyObject = JsonParser.parseString(searchKeyString).getAsJsonObject();

        // 3. searchKey의 모든 속성을 주 JSON 객체에 병합 (Merge)
        for (Map.Entry<String, JsonElement> entry : searchKeyObject.entrySet()) {
            jsonObject.add(entry.getKey(), entry.getValue());
        }

        // 4. 원본 searchKey 필드를 주 JSON 객체에서 제거
        jsonObject.remove("searchKey");

        // 5. 최종 JsonObject를 JSON String으로 변환하여 반환
        return GSON.toJson(jsonObject);
    }
}