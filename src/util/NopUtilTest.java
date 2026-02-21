package util;

import java.util.Optional;

public class NopUtilTest {
    public static void main(String[] args) {

        // 기존 pass / skip / ignore / noop / nop
        NopUtil.pass();
        NopUtil.skip();
        NopUtil.ignore();
        NopUtil.noop();
        NopUtil.nop();

        // Runnable no-op
        Runnable r = NopUtil.runnable();
        r.run(); // 아무 일도 하지 않음

        // Consumer no-op
        Optional<String> maybe = Optional.of("Hello");
        maybe.ifPresent(NopUtil.consumer()); // 아무 일도 하지 않음

        // Function identity
        var f = NopUtil.identityFunction();
        System.out.println(f.apply("Hello")); // Hello 그대로 출력

        System.out.println("✔ 실행 완료 (모든 no-op 활용 예제)");
    }
}
