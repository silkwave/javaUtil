package util;

import java.util.function.Consumer;
import java.util.function.Function;

public final class NopUtil {

    private NopUtil() {} // 인스턴스화 방지

    /** Python 스타일 pass */
    public static void pass() {}

    /** 의미상 skip */
    public static void skip() {}

    /** 의미상 ignore */
    public static void ignore() {}

    /** 의미상 no operation */
    public static void noop() {}

    /** 의미상 nop (no operation) */
    public static void nop() {}

    /** Runnable no-op */
    public static Runnable runnable() {
        return () -> {};
    }

    /** Consumer<T> no-op */
    public static <T> Consumer<T> consumer() {
        return t -> {};
    }

    /** Function<T, R> identity no-op */
    public static <T> Function<T, T> identityFunction() {
        return t -> t;
    }
}
