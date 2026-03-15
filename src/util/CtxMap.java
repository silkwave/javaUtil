package util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * CtxMap: Map<String,Object>를 래핑하여 타입-안전 접근자를 제공하는 스레드-안전 유틸리티 클래스.
 * 내부적으로 ConcurrentHashMap을 사용하며, 메서드 체이닝을 지원합니다.
 */
public class CtxMap implements Serializable {

    private static final long serialVersionUID = 20240114L;

    private final Map<String, Object> storage;

    // ── 생성자 ─────────────────────────────────────────────────────────────

    public CtxMap() {
        this.storage = new ConcurrentHashMap<>();
    }

    public CtxMap(Map<String, Object> initial) {
        this.storage = (initial != null)
                ? new ConcurrentHashMap<>(initial)
                : new ConcurrentHashMap<>();
    }

    // ── 정적 팩토리 ────────────────────────────────────────────────────────

    public static CtxMap of(Map<String, Object> initial) {
        return new CtxMap(initial);
    }

    public static CtxMap empty() {
        return new CtxMap();
    }

    // ── 저장 ───────────────────────────────────────────────────────────────

    public CtxMap put(String key, Object value) {
        storage.put(key, value);
        return this;
    }

    public CtxMap putAll(Map<String, ?> other) {
        if (other != null) {
            storage.putAll(other);
        }
        return this;
    }

    public Object putIfAbsent(String key, Object value) {
        return storage.putIfAbsent(key, value);
    }

    public Object merge(String key, Object value,
                        BiFunction<Object, Object, Object> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        return storage.merge(key, value, remappingFunction);
    }

    // ── 타입-안전 조회 ─────────────────────────────────────────────────────

    /**
     * 타입 안전 조회. 타입 불일치 시 null 반환.
     */
    public <T> T getObject(String key, Class<T> type) {
        Object value = storage.get(key);
        return (value != null && type.isInstance(value)) ? type.cast(value) : null;
    }

    /**
     * Optional 로 감싼 타입 안전 조회.
     */
    public <T> Optional<T> getOptional(String key, Class<T> type) {
        return Optional.ofNullable(getObject(key, type));
    }

    /**
     * 문자열 조회. 기본값 지정 가능.
     */
    public String getString(String key, String defaultValue) {
        Object value = storage.get(key);
        return (value instanceof String s) ? s : defaultValue;
    }

    /**
     * 문자열 조회. 기본값 빈 문자열.
     */
    public String getString(String key) {
        return getString(key, "");
    }

    /**
     * 정수 조회. Number 직접 변환 또는 문자열 파싱. 실패 시 기본값.
     */
    public int getInt(String key, int defaultValue) {
        Object value = storage.get(key);
        if (value instanceof Number n) return n.intValue();
        if (value instanceof String s) {
            try { return Integer.parseInt(s.trim()); } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * long 조회.
     */
    public long getLong(String key, long defaultValue) {
        Object value = storage.get(key);
        if (value instanceof Number n) return n.longValue();
        if (value instanceof String s) {
            try { return Long.parseLong(s.trim()); } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }

    public long getLong(String key) {
        return getLong(key, 0L);
    }

    /**
     * double 조회.
     */
    public double getDouble(String key, double defaultValue) {
        Object value = storage.get(key);
        if (value instanceof Number n) return n.doubleValue();
        if (value instanceof String s) {
            try { return Double.parseDouble(s.trim()); } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }

    public double getDouble(String key) {
        return getDouble(key, 0.0);
    }

    /**
     * BigDecimal 조회. Number 변환 또는 문자열 파싱. 실패 시 기본값.
     * 금액 등 정밀도가 중요한 데이터는 BigDecimal 타입으로 직접 put 권장.
     */
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        Object value = storage.get(key);
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number n)     return BigDecimal.valueOf(n.doubleValue());
        if (value instanceof String s) {
            try { return new BigDecimal(s.trim()); } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }

    public BigDecimal getBigDecimal(String key) {
        return getBigDecimal(key, BigDecimal.ZERO);
    }

    /**
     * boolean 조회. "true","1","y","yes","on" → true.
     */
    public boolean getBoolean(String key) {
        Object value = storage.get(key);
        if (value instanceof Boolean b) return b;
        if (value instanceof String s) {
            return java.util.Set.of("true", "1", "y", "yes", "on")
                                .contains(s.toLowerCase().trim());
        }
        return false;
    }

    /**
     * Map 조회. 반환값은 방어적 복사본.
     */
    public Map<String, Object> getMap(String key) {
        Object value = storage.get(key);
        if (value instanceof Map<?, ?> m) {
            Map<String, Object> result = new HashMap<>();
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                if (entry.getKey() instanceof String k) {
                    result.put(k, entry.getValue());
                }
            }
            return result;
        }
        return null;
    }

    /**
     * List 조회. 타입 불일치 요소는 제외. 반환값은 방어적 복사본.
     */
    public <T> List<T> getList(String key, Class<T> elementType) {
        Object value = storage.get(key);
        if (value instanceof List<?> list) {
            List<T> result = new ArrayList<>();
            for (Object o : list) {
                if (elementType.isInstance(o)) {
                    result.add(elementType.cast(o));
                }
            }
            return result;
        }
        return null;
    }

    // ── 유틸리티 ───────────────────────────────────────────────────────────

    public boolean containsKey(String key) {
        return storage.containsKey(key);
    }

    /**
     * 값이 존재하고, 문자열인 경우 공백이 아닌지 확인.
     */
    public boolean hasText(String key) {
        Object value = storage.get(key);
        if (value instanceof String s) return !s.isBlank();
        return value != null;
    }

    /**
     * 읽기 전용 스냅샷 반환.
     */
    public Map<String, Object> asReadOnlyMap() {
        return Collections.unmodifiableMap(new HashMap<>(storage));
    }

    public Object  remove(String key) { return storage.remove(key); }
    public void    clear()            { storage.clear(); }
    public int     size()             { return storage.size(); }
    public boolean isEmpty()          { return storage.isEmpty(); }

    // ── Object 기본 메서드 ─────────────────────────────────────────────────

    @Override
    public String toString() {
        return "CtxMap" + storage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(storage, ((CtxMap) o).storage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storage);
    }
}