package rxjava;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchExample2 {

    public static void main(String[] args) throws InterruptedException {
        // 카운트 다운이 2인 CountDownLatch 생성
        CountDownLatch latch = new CountDownLatch(2);

        // 첫 번째 작업 스레드 실행
        Thread thread1 = new Thread(new Task(latch));
        thread1.start();

        // 두 번째 작업 스레드 실행
        Thread thread2 = new Thread(new Task(latch));
        thread2.start();

        // 메인 스레드는 두 작업 스레드가 모두 종료될 때까지 대기
        latch.await();

        // 두 작업 스레드가 종료되면 이어지는 작업 실행
        System.out.println("두 작업 스레드가 모두 종료되었습니다.");
    }

    // 각 작업 스레드에서 수행할 작업을 정의한 클래스
    static class Task implements Runnable {
        private final CountDownLatch latch;

        public Task(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                // 일부 작업 수행
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + " 작업 완료");

                // 작업 완료 시 카운트 다운
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
