package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class GsonDemo {
    public static void main(String[] args) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Map 생성 (중첩 DTO 포함)
        Map<String, Object> map = new HashMap<>();
        map.put("idS", "u400");
        map.put("nameS", "김철수");
        map.put("ageS", 35);

        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("streetS", "강남대로 20");
        addressMap.put("cityS", "서울");
        addressMap.put("zipS", "06134");

        Map<String, Object> profileMap = new HashMap<>();
        profileMap.put("emailS", "kim@example.com");
        profileMap.put("phoneS", "010-9876-5432");

        map.put("addressS", addressMap);
        map.put("profileS", profileMap);

        // Map → JSON 문자열 변환
        String jsonString = gson.toJson(map);

        // JSON → UserDto 변환
        UserDto user = gson.fromJson(jsonString, UserDto.class);

        // 결과 출력
        System.out.println(user);
        // JSON 문자열 출력
        System.out.println("\nJSON 출력:\n" + jsonString);
    }
}
