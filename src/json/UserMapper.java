package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class UserMapper {
    
    // Gson 기본 객체 (null 필드 제외 기본 설정)
    private static final Gson gson = new GsonBuilder().create();

    // JSON 예쁘게 출력용 Gson 객체 (재사용)
    private static final Gson PRETTY_GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * Map을 UserDTO 객체로 변환
     * @param userMap 변환할 Map 데이터
     * @return UserDTO 객체
     */
    public static UserDtO2 mapToUserDTO(Map<String, Object> userMap) {
        String json = gson.toJson(userMap);
        return gson.fromJson(json, UserDtO2.class);
    }

    /**
     * UserDTO 객체를 Map으로 변환
     * @param userDTO 변환할 UserDTO 객체
     * @return Map 형태의 데이터
     */
    public static Map<String, Object> mapToUserMap(UserDtO2 userDTO) {
        String json = gson.toJson(userDTO);
        return gson.fromJson(json, Map.class);
    }

    /**
     * 예제용 샘플 User Map 생성
     * @return 샘플 데이터가 담긴 Map
     */
    public static Map<String, Object> createSampleUserMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "홍길동");
        map.put("email", "silkwave@nate.com");
        map.put("age", 30);
        map.put("phoneNumber", "010-1234-5678");
        map.put("address", "서울시 강남구");
        map.put("active", true);
        map.put("role", "USER");
        map.put("createdAt", "2025-05-22T10:00:00Z");
        return map;
    }

    /**
     * 메시지와 함께 객체를 예쁘게 JSON 출력
     * @param message 출력할 메시지
     * @param obj JSON으로 변환할 객체
     */
    public static void printJsonWithMessage(String message, Object obj) {
        System.out.println(message);
        System.out.println(PRETTY_GSON.toJson(obj));
    }
}
