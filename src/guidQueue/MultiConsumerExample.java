package guidQueue;

public class MultiConsumerExample {
    public static void main(String[] args) throws InterruptedException {
        ProducerConsumerUtil util = new ProducerConsumerUtil(5, 3);

        System.out.println(Thread.currentThread().getName() + " 시작");

        // 소비자 먼저 시작
        util.startConsumers(3);
        Thread.sleep(1000);  // 소비자가 실행될 시간을 줌

        // 생산 (큐에 데이터 추가)
        util.produce(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});

        // 충분한 소비 시간을 줌
        Thread.sleep(7000);

        // 소비자 종료
        System.out.println("소비자 종료 신호 전송");
        util.shutdownConsumers(3);
    }
}
