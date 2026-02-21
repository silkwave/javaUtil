package util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Java에서 데이터를 처리하는 네 가지 주요 방식(배열 루프, Iterator, for-each, Stream)을 비교하는 예제 클래스입니다.
 * 모든 방식은 '변환(map)', '필터링(filter)', '제한(limit)', '소비(forEach)'의 동일한 로직을 수행합니다.
 */
public class StreamSample {

    // 💡 최종 결과를 제한하는 상수. 최대 4개의 결과만 처리합니다.
    private static final int LIMIT = 4;

    public static void main(String[] args) {

        // Function: 입력(Integer)을 받아 출력(Integer)으로 변환하는 역할 (map)
        Function<Integer, Integer> multiplyBy5 = n -> n * 5;
        
        // Predicate: 입력(Integer)을 받아 true/false를 반환하여 조건을 판단하는 역할 (filter)
        Predicate<Integer> isEven = n -> n % 2 == 0;
        
        // Consumer: 입력(Integer)을 받아 최종적으로 처리(소비)하는 역할 (forEach)
        Consumer<Integer> printResult = n -> System.out.println("consume " + n);
        
        // 제목 출력을 위한 유틸리티 Consumer
        Consumer<String> printTitle = title -> {
            System.out.println("\n" + title);
            System.out.println("---------------------------------");
        };

        // 0부터 9까지의 숫자를 담은 배열 및 List 생성
        Integer[] numbers = IntStream.range(0, 10).boxed().toArray(Integer[]::new);
        List<Integer> numberList = Arrays.asList(numbers);

        // 🔹 각 반복문 스타일별 출력 및 비교
        
        printTitle.accept("1. Array For Loop (배열 일반 루프)");
        processWithArray(numbers, multiplyBy5, isEven, printResult);

        printTitle.accept("2. Iterator While Loop (Iterator 활용)");
        // List에서 새로운 Iterator를 얻어 전달합니다.
        processWithIterator(numberList.iterator(), multiplyBy5, isEven, printResult);

        printTitle.accept("3. Enhanced For Loop (향상된 for 루프)");
        processWithForEach(numberList, multiplyBy5, isEven, printResult);

        printTitle.accept("4. Java 8 Stream API (선언적 처리)");
        processWithStream(numberList, multiplyBy5, isEven, printResult);
    }

    /**
     * 🔸 배열(Array)과 일반적인 for-each 루프를 사용하여 데이터를 처리합니다.
     * 변환, 필터링, 그리고 제한(LIMIT) 로직을 모두 수동으로 구현해야 합니다.
     */
    private static void processWithArray(
            Integer[] array,
            Function<Integer, Integer> transform,
            Predicate<Integer> filter,
            Consumer<Integer> action) {

        int processedCount = 0; // 제한 로직을 위한 카운터
        for (int n : array) {
            // 1. 변환 (map)
            int transformed = transform.apply(n);
            
            // 2. 필터링 (filter)
            if (filter.test(transformed)) {
                // 3. 소비 (forEach)
                action.accept(transformed);
                
                // 4. 제한 (limit) 및 루프 중단 (break) 로직 수동 구현
                if (++processedCount >= LIMIT) break;
            }
        }
    }

    /**
     * 🔸 Iterator와 while 루프를 사용하여 데이터를 처리합니다.
     * next() 호출 전에 hasNext()로 요소 존재 여부를 확인해야 하며, 제한 로직은 여전히 수동입니다.
     */
    private static void processWithIterator(
            Iterator<Integer> iterator,
            Function<Integer, Integer> transform,
            Predicate<Integer> filter,
            Consumer<Integer> action) {

        int processedCount = 0; // 제한 로직을 위한 카운터
        while (iterator.hasNext()) { // 요소가 남아있는지 확인
            // 1. 변환 (map) 및 다음 요소 가져오기
            int transformed = transform.apply(iterator.next());
            
            // 2. 필터링 (filter)
            if (filter.test(transformed)) {
                // 3. 소비 (forEach)
                action.accept(transformed);
                
                // 4. 제한 (limit) 및 루프 중단 (break) 로직 수동 구현
                if (++processedCount >= LIMIT) break;
            }
        }
    }

    /**
     * 🔸 향상된 for 루프(Enhanced For Loop)를 사용하여 List 데이터를 처리합니다.
     * processWithArray와 로직 구현 방식은 거의 동일하며, Collection에 적합합니다.
     */
    private static void processWithForEach(
            List<Integer> list,
            Function<Integer, Integer> transform,
            Predicate<Integer> filter,
            Consumer<Integer> action) {

        int processedCount = 0; // 제한 로직을 위한 카운터
        for (int n : list) {
            // 1. 변환 (map)
            int transformed = transform.apply(n);
            
            // 2. 필터링 (filter)
            if (filter.test(transformed)) {
                // 3. 소비 (forEach)
                action.accept(transformed);
                
                // 4. 제한 (limit) 및 루프 중단 (break) 로직 수동 구현
                if (++processedCount >= LIMIT) break;
            }
        }
    }

    /**
     * 🔸 Java 8 Stream API를 사용하여 데이터를 처리합니다.
     * 모든 로직이 메서드 체인 형태로 '선언적'으로 표현되어 가독성이 높고 간결합니다.
     * - map, filter, limit: 중간 연산 (Intermediate Operations)
     * - forEach: 최종 연산 (Terminal Operation)
     */
    private static void processWithStream(
            List<Integer> list,
            Function<Integer, Integer> transform,
            Predicate<Integer> filter,
            Consumer<Integer> action) {

        list.stream()
                .map(transform)     // 1. 변환: 모든 요소에 대해 transform 함수 적용
                .filter(filter)    // 2. 필터링: filter Predicate가 true인 요소만 통과
                .limit(LIMIT)      // 3. 제한: 상위 LIMIT개 요소만 통과 (break 로직을 추상화)
                .forEach(action);  // 4. 소비: 최종적으로 남은 요소에 대해 action Consumer 적용
    }
}
