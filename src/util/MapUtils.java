package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * MapUtils: 객체(Object, DTO, Map 등)를 Map<String, Object>로 변환하는 유틸리티.
 * 내부적으로 Jackson의 ObjectMapper.convertValue를 사용하여 unchecked cast 경고를 방지합니다.
 */
public final class MapUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private MapUtils() {
    }

    /**
     * Object를 Map<String, Object>로 변환합니다. source가 null이면 null을 반환합니다.
     */
    public static Map<String, Object> toMap(Object source) {
        if (source == null)
            return null;
        return MAPPER.convertValue(source, new TypeReference<Map<String, Object>>() {
        });
    }
}