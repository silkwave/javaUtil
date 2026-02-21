package guid;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GUIDTest {
    // GUID 중복 검증을 수행하는 Callable 작업 클래스
    static class GUIDGenerationTask implements Callable<Integer> {
        private final int numberOfGUIDs; // 각 스레드에서 생성할 GUID의 개수
        private final Set<String> globalGuidSet; // 전체 GUID를 저장하는 Set (중복 체크)

        // 생성자: GUID 개수와 전체 GUID를 저장할 Set을 받음
        public GUIDGenerationTask(int numberOfGUIDs, Set<String> globalGuidSet) {
            this.numberOfGUIDs = numberOfGUIDs;
            this.globalGuidSet = globalGuidSet;
        }

        @Override
        public Integer call() {
            int duplicateCount = 0; // 중복 GUID의 개수를 세는 변수

            // 주어진 개수만큼 GUID를 생성하고 중복 체크
            for (int i = 0; i < numberOfGUIDs; i++) {
                // 고유 GUID 생성
                String guid = KubernetesUniqueGUIDGenerator.generateUniqueGUID();
                //  System.out.println(i + " GUID [" + guid + "]\n");

                // GUID를 Set에 추가하고 중복이 발생하면 중복 카운트 증가
                if (!globalGuidSet.add(guid)) {
                    duplicateCount++; // 중복 GUID 발견 시 증가
                }
            }

            // 중복된 GUID 개수를 반환
            return duplicateCount;
        }
    }

    public static void main(String[] args) throws Exception {
        int totalGUIDs = 5000000; // 생성할 GUID의 총 개수
        int threadCount = 50; // GUID 생성을 분할할 스레드의 수
        int guidPerThread = totalGUIDs / threadCount; // 각 스레드가 처리할 GUID의 수

        // 스레드 안전한 ConcurrentSet 생성 (GUID의 중복을 안전하게 체크)
        Set<String> globalGuidSet = ConcurrentHashMap.newKeySet();

        // 고정된 크기의 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        Set<Future<Integer>> futures = new HashSet<>(); // 각 스레드의 실행 결과를 저장할 Set

        // GUID 생성 작업을 각 스레드에 할당
        for (int i = 0; i < threadCount; i++) {
            futures.add(executorService.submit(new GUIDGenerationTask(guidPerThread, globalGuidSet)));
        }

        // 각 스레드의 결과(중복된 GUID의 개수)를 합산
        int totalDuplicates = 0;
        for (Future<Integer> future : futures) {
            totalDuplicates += future.get(); // 각 스레드에서 반환된 중복 수를 더함
        }

        // 총 중복 GUID의 개수를 출력
        System.out.println("Total duplicates found: " + totalDuplicates);

        executorService.shutdown(); // 스레드 풀 종료
    }
}
