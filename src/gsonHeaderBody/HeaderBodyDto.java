package gsonHeaderBody;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class HeaderBodyDto {

    @SerializedName("requestIdS")
    @JsonProperty("requestIdJ")
    @JsonAlias("requestIdJ")    
    private String requestId;

    @SerializedName("sourceS")
    @JsonProperty("sourceJ")
    private String source;

    @SerializedName("timestampS")
    @JsonProperty("timestampJ")
    private Long timestamp;

    @SerializedName("idS")
    @JsonProperty("idJ")
    private String id;

    @SerializedName("nameS")
    @JsonProperty("nameJ")
    private String name;

    @SerializedName("ageS")
    @JsonProperty("ageJ")
    private Integer age;

    @SerializedName("addressCountS")
    @JsonProperty("addressCountJ")
    private long addressCount;

    @SerializedName("addressS")
    @JsonProperty("addressJ")
    private List<AddressDto> address;

    @SerializedName("profileS")
    @JsonProperty("profileJ")
    private ProfileDto profile;

    // ------------------------
    // 내부 클래스
    // ------------------------
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
