package json;

import com.google.gson.*;
import java.lang.reflect.Type;

public class SearchKeyAdapter implements JsonSerializer<String> {

    @Override
    public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
        try {
            // JSON 문자열을 JSON 객체로 변환
            return JsonParser.parseString(src);
        } catch (Exception e) {
            // JSON 변환 실패 시 그냥 문자열 출력
            return new JsonPrimitive(src);
        }
    }
}
