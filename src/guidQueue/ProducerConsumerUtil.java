package guidQueue;

import java.util.concurrent.*;

public class ProducerConsumerUtil {
    private final LinkedBlockingQueue<Integer> queue;
    private final ExecutorService executor;

    public ProducerConsumerUtil(int queueSize, int consumerCount) {
        this.queue = new LinkedBlockingQueue<>(queueSize);
        this.executor = Executors.newFixedThreadPool(consumerCount);
    }

    public void produce(int[] items) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " produce 시작");

        for (int item : items) {
            System.out.println(Thread.currentThread().getName() + " produce: " + item);
            queue.put(item);  // 큐가 가득 차면 블로킹됨
        }

        System.out.println("모든 생산 완료");
    }

    public void startConsumers(int consumerCount) {
        System.out.println("소비자 시작");
        for (int i = 0; i < consumerCount; i++) {
            System.out.println("소비자 " + (i + 1) + " 실행");
            executor.execute(new ConsumerTask(queue));
        }
    }

    public void shutdownConsumers(int consumerCount) throws InterruptedException {
        System.out.println("소비자 종료 시작");

        // 각 소비자에게 종료 신호 전달
        for (int i = 0; i < consumerCount; i++) {
            queue.put(-1);
            System.out.println("소비자 종료 신호 보냄: " + (i + 1));
        }

        executor.shutdown();
        if (executor.awaitTermination(5, TimeUnit.SECONDS)) {
            System.out.println("소비자 정상 종료 완료");
        } else {
            System.out.println("소비자 강제 종료 필요");
        }
    }
}
