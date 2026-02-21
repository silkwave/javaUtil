package json;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class JacksonDemo {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Map 생성 (중첩 DTO 포함)
        Map<String, Object> map = new HashMap<>();
        map.put("idJ", "u300");
        map.put("nameJ", "홍길동");
        map.put("ageJ", 28);

        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("streetJ", "테헤란로 10");
        addressMap.put("cityJ", "서울");
        addressMap.put("zipJ", "06134");

        Map<String, Object> profileMap = new HashMap<>();
        profileMap.put("emailJ", "hong@example.com");
        profileMap.put("phoneJ", "010-1234-5678");

        map.put("addressJ", addressMap);
        map.put("profileJ", profileMap);

        // Map → UserDto 변환
        UserDto user = mapper.convertValue(map, UserDto.class);

        // 결과 출력
        System.out.println(user.getId());                       // u300
        System.out.println(user.getName());                     // 홍길동
        System.out.println(user.getAge());                      // 28
        System.out.println(user.getAddress().getStreet());     // 테헤란로 10
        System.out.println(user.getAddress().getCity());       // 서울
        System.out.println(user.getAddress().getZip());        // 06134
        System.out.println(user.getProfile().getEmail());      // hong@example.com
        System.out.println(user.getProfile().getPhone());      // 010-1234-5678

        // JSON 문자열로 변환
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
        System.out.println("\nJSON 출력:\n" + jsonString);
    }
}
