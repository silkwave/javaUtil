package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class RemoveNullValues {

    public static Map<String, Object> removeMapNullValues(JsonObject jsonObject) {
        
        jsonObject.entrySet().removeIf(entry -> {
            JsonElement value = entry.getValue();

            if (value.isJsonNull()) {
                return true;
            }
            if (value.isJsonPrimitive()) {
                return value.getAsString().isEmpty(); // 빈 문자열 제거
            } else if (value.isJsonObject()) {
                removeMapNullValues(value.getAsJsonObject());
                return value.getAsJsonObject().entrySet().isEmpty();
            } else if (value.isJsonArray()) {
                return value.getAsJsonArray().isEmpty();
            }
            return false;
        });

        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<String, Object>>() {}.getType();
        return new Gson().fromJson(jsonObject, type);
    }

    // ✅ 테스트용 main 메서드
    public static void main(String[] args) {
        Map<String, Object> jsonMap = new LinkedHashMap<>();

        jsonMap.put("name", "John");
        jsonMap.put("emptyString", "");
        jsonMap.put("nullField", null);
        jsonMap.put("age", 30);

        Map<String, Object> addressMap = new LinkedHashMap<>();
        addressMap.put("street", "");
        addressMap.put("city", "Seoul");
        addressMap.put("zipcode", "");
        jsonMap.put("address", addressMap);

        jsonMap.put("hobbies", new Object[]{}); // 빈 배열
        jsonMap.put("skills", new Object[]{"Java", "", null});

        Map<String, Object> socialMap = new LinkedHashMap<>();
        socialMap.put("twitter", "");
        socialMap.put("github", "");

        Map<String, Object> profileMap = new LinkedHashMap<>();
        profileMap.put("bio", "");
        profileMap.put("cnt", "1");
        profileMap.put("social", socialMap);
        jsonMap.put("profile", profileMap);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Map → JsonObject
        JsonObject jsonObject = gson.toJsonTree(jsonMap).getAsJsonObject();

        System.out.println("⛔ 원본 JSON:");
        System.out.println(gson.toJson(jsonObject));

        Map<String, Object> cleanedMap = removeMapNullValues(jsonObject);

        System.out.println("\n✅ 정제 후 결과 Map:");
        System.out.println(gson.toJson(cleanedMap));
    }
}
