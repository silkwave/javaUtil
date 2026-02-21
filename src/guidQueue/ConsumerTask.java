package guidQueue;

import java.util.concurrent.LinkedBlockingQueue;

public class ConsumerTask implements Runnable {
    private final LinkedBlockingQueue<Integer> queue;

    public ConsumerTask(LinkedBlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println(Thread.currentThread().getName() + " 대기 중...");
                Integer value = queue.take(); // 큐에서 데이터 가져오기
                System.out.println(Thread.currentThread().getName() + " 소비: " + value);

                if (value == -1) {
                    System.out.println(Thread.currentThread().getName() + " 종료됨.");
                    break;
                }

                Thread.sleep(1000); // 처리 속도 조절
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 인터럽트 발생, 종료됨.");
            Thread.currentThread().interrupt();
        }
    }
}
