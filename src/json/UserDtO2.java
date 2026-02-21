package json;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDtO2 {
    private String name;
    private String email;
    private String phoneNumber;  // 전화번호
    private Integer age;         // 나이
    private String address;      // 주소
    private Boolean active;      // 활성화 여부
    private String role;         // 사용자 역할 (예: ADMIN, USER)
    private String createdAt;    // 생성일자 (ISO8601 문자열 등)   
}

