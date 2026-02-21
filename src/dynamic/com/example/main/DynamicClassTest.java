package dynamic.com.example.main;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DynamicClassTest {

    private static final int    RELOAD_INTERVAL_SECONDS = 3                            ; // 클래스 리로드 간격(초)
    private static final String CLASS_NAME              = "dynamic.com.example.mod.SampleClass"; // 실행할 클래스 이름
    private static final String METHOD_NAME             = "myMethod1"                  ; // 실행할 메서드 이름
    private static final String JAR_FILE_NAME           = "mod"                        ; // JAR 파일 이름

    public static void main(String[] args) {
        DynamicClassHandler handler = new DynamicClassHandler();

        // 입력 맵 생성
        Map<String, String> input = createInputMap(); 

        // 작업 예약
        scheduleTask(handler, input);
    }

    // 주기적으로 작업을 실행하도록 스케줄을 설정하는 메서드
    private static void scheduleTask(DynamicClassHandler handler, Map<String, String> input) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // 지정된 시간 간격으로 작업을 반복 실행
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // 동적으로 실행할 파라미터 설정
                Map<String, Object> params = new HashMap<>();
                params.put("className"  , CLASS_NAME   );
                params.put("methodName" , METHOD_NAME  );
                params.put("input"      , input        );
                params.put("jarFileName", JAR_FILE_NAME);

                // 동적으로 클래스를 처리하고 결과를 얻기
                Map<String, String> output = handler.handleDynamicClass(params);

                // 결과 출력
                if (output != null) {
                    printOutput(output); // 출력
                }

            } catch (IOException | URISyntaxException e) {
                System.err.println("작업 실행 중 오류 발생: " + e.getMessage());
            }
        }, 0, RELOAD_INTERVAL_SECONDS, TimeUnit.SECONDS); // 0초 후 시작, 주기적으로 실행

        // 프로그램 종료 시 스케줄러 종료
        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::shutdown));
    }

    // 입력 맵 생성
    private static Map<String, String> createInputMap() {
        Map<String, String> input = new HashMap<>();
        input.put("argument1", "value1");  // 첫 번째 인자
        input.put("argument2", "value2");  // 두 번째 인자
        return input;
    }

    // 메서드 실행 결과를 출력하는 메서드
    private static void printOutput(Map<?, ?> output) {
        System.out.println("메서드 결과:");
        output.forEach((key, value) -> System.out.println(key + ": " + value)); // 출력된 값들
        System.out.println("\n\n\n\n");
    }
}
