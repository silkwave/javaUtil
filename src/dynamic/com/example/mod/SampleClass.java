package dynamic.com.example.mod;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class SampleClass {
    private Map<String, String> input;

    // 생성자에 맵을 받도록 변경
    public SampleClass(Map<String, String> input) {
        this.input = input;
    }

    // myMethod1 메서드: 입력을 받고, 현재 날짜와 시간 출력, 입력 값 처리 및 출력 값 반환
    public Map<String, String> myMethod1() {

        System.out.println("\n");
        // SampleClass가 위치한 JAR 파일의 경로 출력
        String jarFilePath = SampleClass.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        System.out.println("MyClass1 파일 위치: " + jarFilePath);
        System.out.println("MyClass1의 메소드가 호출되었습니다.");

        // 현재 날짜와 시간 출력
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        System.out.println("현재    날짜: " + currentDate);
        System.out.println("현재    시간: " + currentTime);

        // 입력받은 값을 처리하고 출력 예시를 반환
        System.out.println("MyClass1의 메소드에서 받은 입력:");
        input.forEach((key, value) -> System.out.println(key + ": " + value));

        Map<String, String> output = new LinkedHashMap<>();
        Random random = new Random();
        int k = random.nextInt(10); // 0부터 9까지의 랜덤한 별 개수 생성

        // 랜덤한 곱셈 결과와 랜덤한 별 개수를 출력
        for (int i = k; i <= 9; i++) {
            for (int j = 1; j <= 2; j++) {
                int result = i * j;
                int starCount = random.nextInt(10); // 0부터 9까지의 랜덤한 별 개수 생성
                output.put("별 출력을 위한 메서드 별    님 " + i + " * " + j, String.valueOf(result) + " (" + printStars(starCount) + ")");
            }
        }

        return output;
    }

    // 별 출력을 위한 메서드
    private String printStars(int count) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < count; i++) {
            stars.append("x*"); // "x*"를 count 만큼 추가
        }
        return stars.toString();
    }
}
