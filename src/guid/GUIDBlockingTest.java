package guid;


import java.util.*;
import java.util.concurrent.*;

import guid.GUIDBlockingQueue.GUIDGeneratorThread;

public class GUIDBlockingTest {

    // GUID 생성 및 중복 카운트
    private static int generateGUIDsAndCountDuplicates(GUIDBlockingQueue guidBlockingQueue, int threadCount) throws InterruptedException, ExecutionException {
        Set<String> globalGuidSet = ConcurrentHashMap.newKeySet();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        Set<Future<Integer>> futures = new HashSet<>();

        // 스레드 생성 및 실행
        for (int i = 0; i < threadCount; i++) {
            futures.add(executorService.submit(new GUIDGenerationTask(globalGuidSet, guidBlockingQueue)));
        }

        int totalDuplicates = 0;
        // 모든 스레드의 결과를 합산
        for (Future<Integer> future : futures) {
            totalDuplicates += future.get();
        }

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        return totalDuplicates;
    }

    // GUID 생성 작업을 수행하는 클래스
    static class GUIDGenerationTask implements Callable<Integer> {
        private final Set<String> globalGuidSet;
        private final GUIDBlockingQueue guidBlockingQueue;

        public GUIDGenerationTask(Set<String> globalGuidSet, GUIDBlockingQueue guidBlockingQueue) {
            this.globalGuidSet = globalGuidSet;
            this.guidBlockingQueue = guidBlockingQueue;
        }

        @Override
        public Integer call() throws InterruptedException {
            int duplicateCount = 0;

            // 스레드가 인터럽트될 때까지 GUID 생성 및 중복 체크
            while (!Thread.interrupted()) {
                String guid = guidBlockingQueue.getGUID(); // 큐에서 GUID 가져오기

                System.out.println( " GUID [" + guid + "]");

                if (!globalGuidSet.add(guid)) {
                    System.err.println("⚠ 중복 GUID 발견: " + guid);
                    duplicateCount++;
                }
                Thread.sleep(10);
            }

            return duplicateCount;
        }
    }

    public static void main(String[] args) {
        try {
            GUIDBlockingQueue guidBlockingQueue = new GUIDBlockingQueue(100); // 큐 크기 100으로 설정
            GUIDGeneratorThread generatorThread = new GUIDGeneratorThread(guidBlockingQueue.getQueue());
            generatorThread.start();

            int threadCount = 4;
            int totalDuplicates = generateGUIDsAndCountDuplicates(guidBlockingQueue, threadCount);  // 큐 전달
            System.out.println("✅ 중복 GUID 개수: " + totalDuplicates);

            generatorThread.interrupt();
            generatorThread.join();

        } catch (Exception e) {
            System.err.println("❌ GUID 생성 중 오류 발생: " + e.getMessage());
        }
    }
}
