package json;

import java.util.Map;

public class Main {
    public static void main(String[] args) {

        // mkdir -p lib
        // wget
        // ttps://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
        // -O lib/gson-2.10.1.jar

        // 샘플 유저 정보를 담은 Map 생성
        Map<String, Object> userMap = UserMapper.createSampleUserMap();

        // Map → UserDTO 객체로 변환
        UserDtO2 user = UserMapper.mapToUserDTO(userMap);

        // 이메일이 존재하면 null로 설정하여 JSON 직렬화 시 제외되도록 처리
        if (user.getEmail() != null) {
            user.setEmail(null);
        } else {
            System.out.println("이메일이 없습니다.");
        }

        // Map → DTO 변환 결과를 예쁘게 JSON 출력
        UserMapper.printJsonWithMessage("Map → DTO 변환 결과:", user);

        // UserDTO → Map 변환
        Map<String, Object> convertedMap = UserMapper.mapToUserMap(user);

        // DTO → Map 변환 결과를 예쁘게 JSON 출력
        UserMapper.printJsonWithMessage("DTO → Map 변환 결과:", convertedMap);

  
    }  
    
}
