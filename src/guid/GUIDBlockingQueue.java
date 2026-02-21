package guid;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GUIDBlockingQueue {

    // GUIDBlockingQueue 객체를 저장할 맵
    public static Map<String, Object> ctxMap = new HashMap<>();

    // AtomicLong을 사용하여 GUID 생성 시 고유한 숫자 값을 증가시킴
    private static final AtomicLong atomicCounter = new AtomicLong(0);

    // 기본 컨테이너 이름
    private static final String DEFAULT_CONTAINER_NAME = "OSI01";

    // GUID를 저장할 BlockingQueue
    private final BlockingQueue<String> queue;

    // BlockingQueue의 용량을 설정하는 생성자
    public GUIDBlockingQueue(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    // BlockingQueue를 반환하는 getter
    public BlockingQueue<String> getQueue() {
        return queue;
    }

    // GUID를 생성하는 작업을 수행할 스레드 클래스
    public static class GUIDGeneratorThread extends Thread {

        private final BlockingQueue<String> queue;

        // 생성자에서 BlockingQueue를 전달받음
        public GUIDGeneratorThread(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                // 스레드가 종료될 때까지 GUID를 생성하여 큐에 넣음
                while (!Thread.interrupted()) {
                    queue.put(getGUIDSeq());
                }
            } catch (InterruptedException e) {
                // 인터럽트 발생 시 스레드를 종료
                Thread.currentThread().interrupt();
            }
        }
    }

    // 환경변수에서 컨테이너 이름을 읽어오는 메소드, 없으면 기본값 사용
    @SuppressWarnings("unused")
    private static String getContainerName() {
        return Optional.ofNullable(System.getenv("CONTAINER_NAME"))
                .filter(name -> !name.isEmpty())
                .orElse(DEFAULT_CONTAINER_NAME);
    }

    // 현재 스레드의 PID와 스레드 ID를 결합하여 고유한 PID 값을 생성
    @SuppressWarnings("deprecation")
    private static String getThreadPID() {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        return jvmName.split("@")[0] + String.format("%02d", Thread.currentThread().getId());
    }

    // AtomicLong을 사용하여 증가하는 숫자 기반의 GUID 생성
    private static String generateAtomicGUID() {
        return String.format("%07d", atomicCounter.incrementAndGet());
    }

    // 고유한 GUID를 생성하는 메소드
    public static String getGUIDSeq() {
        // PID와 Atomic GUID를 합쳐서 고유한 GUID를 생성
        return String.format("%s:%s", getThreadPID(), generateAtomicGUID()).toUpperCase();
    }

    // 현재 날짜와 시간을 "yyyyMMddHHmmss" 형식으로 반환
    private static String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    // 생성된 GUID가 32자 이상일 경우 자르고, 그렇지 않으면 0으로 채워서 길이를 맞춤
    private static String getFormatGUID(String rawGUID) {
        return rawGUID.length() >= 32 ? rawGUID.substring(0, 32) : String.format("%-32s", rawGUID).replace(' ', '0');
    }

    // 큐에서 GUID를 가져오고 현재 날짜를 추가하여 최종 GUID를 반환
    public String getGUID() throws InterruptedException {
        String guidFromQueue = queue.take();
        return getFormatGUID(String.format("%s:%s", getCurrentDate(), guidFromQueue).toUpperCase());
    }

    public static void main(String[] args) throws InterruptedException {
        // GUIDBlockingQueue 객체 생성 (용량 100)
        GUIDBlockingQueue guidBlockingQueue = new GUIDBlockingQueue(100);
        // ctxMap에 GUIDBlockingQueue 객체를 저장
        ctxMap.put("GUID", guidBlockingQueue);

        // GUIDGeneratorThread 스레드 시작
        GUIDGeneratorThread generatorThread = new GUIDGeneratorThread(guidBlockingQueue.getQueue());
        generatorThread.start();

        printguid();

        // 스레드 종료
        generatorThread.interrupt();
        generatorThread.join();
    }

    // GUID를 출력하는 메소드
    private static void printguid() {

        for (int i = 0; i < 10; i++) {
            try {
                // ctxMap에서 GUIDBlockingQueue 객체를 가져옴
                var guidBlockingQueue = (GUIDBlockingQueue) ctxMap.get("GUID");

                // GUID를 가져와 출력
                var guid = guidBlockingQueue.getGUID();
                System.out.println("GUID: " + guid);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted while getting GUID.");
            }
        }

    }
}
