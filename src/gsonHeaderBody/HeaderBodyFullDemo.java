package gsonHeaderBody;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.TypeReference;
import java.lang.reflect.Type;

import java.util.HashMap;
import java.util.Map;

public class HeaderBodyFullDemo {

    public static void main(String[] args) throws Exception {

        // 원본 JSON 문자열 (Header + Body)
        String jsonString = """
            {
              "Header": {
                "requestIdS": "req-1001"
              },
              "Body": {
                "idS": "u400",
                "nameS": "김철수",
                "ageS": 35,
                "addressS": [
                  {
                    "streetS": "강남대로 20",
                    "cityS": "서울",
                    "zipS": "06134"
                  },
                  {
                    "streetS": "역삼로 10",
                    "cityS": "서울",
                    "zipS": "06133"
                  }
                ],
                "profileS": {
                  "emailS": "kim@example.com",
                  "phoneS": "010-9876-5432"
                }
              }
            }
            """;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // --------------------------
        // 1️⃣ JSON → Map 변환 (Gson)
        // --------------------------
Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
Map<String, Object> OagInMap = gson.fromJson(jsonString, mapType);
        // --------------------------
        Map<String, Object> mergedMap = new HashMap<>();
        Map<String, Object> headerMap = (Map<String, Object>) OagInMap.get("Header");
        Map<String, Object> bodyMap   = (Map<String, Object>) OagInMap.get("Body");

        if (headerMap != null) mergedMap.putAll(headerMap);
        if (bodyMap != null)   mergedMap.putAll(bodyMap);

        // --------------------------
        // 3️⃣ Map → DTO 변환 (Gson)
        // --------------------------
        HeaderBodyDto dto = gson.fromJson(gson.toJson(mergedMap), HeaderBodyDto.class);

        // Address 개수 예시 추가
        dto.setAddressCount(dto.getAddress() == null ? 0 : dto.getAddress().size());

        System.out.println("===== Gson DTO 출력 =====");
        System.out.println("OagOutDto " + gson.toJson(dto));

        // --------------------------
        // 4️⃣ DTO → JSON → Map 변환 (Jackson)
        // --------------------------
Map<String, Object> EicOutmap = mapper.readValue(mapper.writeValueAsString(dto), new TypeReference<Map<String, Object>>() {});
    }
}
