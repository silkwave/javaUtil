package util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CompletionServiceTest {

    // 대기 시간 (밀리초 단위)
    private static final int waittime = 200;
    // 쓰레드 풀의 크기 (동시에 실행할 수 있는 쓰레드의 개수)
    private static final int numberOfThreadsInThePool = 3;

    // 출력할 요청 목록
    private final List<String> printRequests = Arrays.asList(
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
    );

    // 1. 일반적인 순차적인 출력
    void normalLoop() {
        for (String image : printRequests) {
            try {
                Thread.sleep(waittime); // 대기 시간 동안 슬립
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print(image); // 출력
        }
    }

    // 2. ExecutorService를 사용하여 멀티쓰레드로 출력
    void normalExecutorService() {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreadsInThePool); // 고정된 크기의 쓰레드 풀 생성
        try {
            Set<Future<String>> printTaskFutures = new HashSet<Future<String>>(); // Future 객체를 저장할 Set 생성
            // printRequests에 있는 각 항목을 처리할 쓰레드 작업 제출
            for (final String printRequest : printRequests) {
                printTaskFutures.add(executor.submit(new Printer(printRequest))); // 각 작업을 ExecutorService에 제출
            }
            // Future 객체에서 결과를 받아 출력
            for (Future<String> future : printTaskFutures) {
                System.out.print(future.get()); // 결과 출력
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt(); // 예외 발생 시 현재 쓰레드 중단
        } finally {
            if (executor != null) {
                executor.shutdownNow(); // Executor 종료
            }
        }
    }

    // 3. CompletionService를 사용하여 완료된 작업 결과를 빠르게 처리
    void completionService() {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreadsInThePool); // 고정된 크기의 쓰레드 풀 생성
        CompletionService<String> completionService = new ExecutorCompletionService<String>(executor); // CompletionService 생성
        // printRequests에 있는 각 항목을 처리할 작업을 CompletionService에 제출
        for (final String printRequest : printRequests) {
            completionService.submit(new Printer(printRequest)); // 각 작업을 CompletionService에 제출
        }
        try {
            // 결과가 완료되는 대로 받아서 출력
            for (int t = 0, n = printRequests.size(); t < n; t++) {
                Future<String> f = completionService.take(); // 완료된 작업의 Future 객체를 가져옴
                System.out.print(f.get()); // 결과 출력
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 예외 발생 시 현재 쓰레드 중단
        } catch (ExecutionException e) {
            Thread.currentThread().interrupt(); // 예외 발생 시 현재 쓰레드 중단
        } finally {
            if (executor != null) {
                executor.shutdownNow(); // Executor 종료
            }
        }

    }

    // 출력 작업을 처리하는 Printer 클래스 (Callable 구현)
    private class Printer implements Callable<String> {

        private final String toPrint; // 출력할 문자열

        public Printer(String toPrint) {
            this.toPrint = toPrint;
        }

        // call 메서드에서 문자열을 출력하고 대기 시간 후 반환
        public String call() {
            try {
                Thread.sleep(waittime); // 대기 시간 동안 슬립
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return toPrint; // 출력할 문자열 반환
        }
    }

    // 프로그램의 실행 시작점
    public static void main(String[] args) {
        // 일반적인 ExecutorService 실행
        System.out.println("Normal Executor Service");
        long start = System.currentTimeMillis(); // 시작 시간 기록
        new CompletionServiceTest().normalExecutorService(); // 실행
        System.out.println();
        System.out.println("Execution time : " + (System.currentTimeMillis() - start)); // 실행 시간 출력

        // CompletionService 실행
        System.out.println("Completion Service");
        start = System.currentTimeMillis(); // 시작 시간 기록
        new CompletionServiceTest().completionService(); // 실행
        System.out.println();
        System.out.println("Execution time : " + (System.currentTimeMillis() - start)); // 실행 시간 출력

        // 일반적인 순차적 출력 실행
        System.out.println("Normal Loop");
        start = System.currentTimeMillis(); // 시작 시간 기록
        new CompletionServiceTest().normalLoop(); // 실행
        System.out.println();
        System.out.println("Execution time : " + (System.currentTimeMillis() - start)); // 실행 시간 출력

    }
}
