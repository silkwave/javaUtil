package guidTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GuidTest {
    // GuidQueue 인스턴스를 전역적으로 선언하여 다른 클래스에서 접근할 수 있도록 public static으로 설정
    public static GuidQueue guidQueue; // 다른 클래스에서 접근할 수 있도록 public static으로 선언

    public static void main(String[] args) throws InterruptedException {
        // 큐 크기 5로 GuidQueue 인스턴스를 생성 (이제 static 변수로 사용)
        guidQueue = GuidQueue.getInstance(10000); // 큐 크기 설정: 5

        // 생산자 스레드 생성 및 시작
        Thread producerThread = new Thread(new GuidProducer(guidQueue)); 
        producerThread.start(); // 생산자 스레드 시작

        // BizProc에서 GUID 생성 및 출력 작업을 분리하여 실행
        BizProc bizProc = new BizProc(); 
        bizProc.processGUIDs(); // 3개의 GUID를 생성하여 출력하는 작업 수행

        // 소비자 스레드 풀을 생성 (3개의 소비자 스레드로 구성)
        ExecutorService executor = Executors.newFixedThreadPool(3); 

        // 3개의 소비자 스레드를 실행하여 큐에서 데이터를 소비
        for (int i = 0; i < 1000; i++) {
            executor.submit(new GuidConsumer(guidQueue)); // 소비자 스레드 제출
        }

        // 생산자 스레드가 끝날 때까지 대기
        producerThread.join(); // 생산자 스레드 종료 대기

        // 소비자 스레드 풀 종료
        executor.shutdown(); // 스레드 풀 종료
        // 소비자들이 작업을 완료할 때까지 최대 10초 동안 대기
        executor.awaitTermination(10, TimeUnit.SECONDS); // 소비자들의 종료 대기
    }
}
