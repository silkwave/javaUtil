package rxjava;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchExample {

    public static void main(String[] args) {
        // 3개의 작업 완료를 기다리는 CountDownLatch 생성
        CountDownLatch latch = new CountDownLatch(3);

        // 3개의 스레드 생성 및 시작
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                // 작업 수행
                System.out.println("작업 " + Thread.currentThread().getName() + " 완료");

                // 작업 완료 후 CountDownLatch 카운트 감소
                latch.countDown();
            }).start();
        }

        // 3개의 작업 완료될 때까지 메인 스레드 대기
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 모든 작업 완료 후 메시지 출력
        System.out.println("모든 작업 완료!");
    }
}
