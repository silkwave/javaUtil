package json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class UserDto {

    @SerializedName("idS")
    @JsonProperty("idJ")
    private String id;

    @SerializedName("nameS")
    @JsonProperty("nameJ")
    private String name;

    @SerializedName("ageS")
    @JsonProperty("ageJ")
    private Integer age;

    // 중첩 DTO 1: 주소 정보
    @SerializedName("addressS")
    @JsonProperty("addressJ")
    private AddressDto address;

    // 중첩 DTO 2: 프로필 정보
    @SerializedName("profileS")
    @JsonProperty("profileJ")
    private ProfileDto profile;

    // ---------------------------
    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AddressDto {
        @SerializedName("streetS")
        @JsonProperty("streetJ")
        private String street;

        @SerializedName("cityS")
        @JsonProperty("cityJ")
        private String city;

        @SerializedName("zipS")
        @JsonProperty("zipJ")
        private String zip;
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ProfileDto {
        @SerializedName("emailS")
        @JsonProperty("emailJ")
        private String email;

        @SerializedName("phoneS")
        @JsonProperty("phoneJ")
        private String phone;
    }
}
