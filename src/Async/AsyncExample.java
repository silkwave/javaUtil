package Async;

import java.util.concurrent.CompletableFuture;

public class AsyncExample {

    public static void main(String[] args) {
        System.out.println("메인 스레드 시작");

        // 비동기 작업 실행
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                // 비동기 작업 시뮬레이션
                System.out.println("비동기 작업 시작");
                Thread.sleep(2000);  // 2초 대기
                System.out.println("비동기 작업 완료");
            } catch (InterruptedException e) {
                System.err.println("작업이 중단되었습니다: " + e.getMessage());
            }
        });

        // 메인 스레드에서 다른 작업 수행
        for (int i = 0; i < 5; i++) {
            System.out.println("메인 스레드 작업: " + (i + 1));
            try {
                Thread.sleep(500);  // 0.5초 대기
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 비동기 작업이 완료될 때까지 대기
        future.join();  // 결과를 기다림 (Optional)
        System.out.println("메인 스레드 종료");
    }
}
