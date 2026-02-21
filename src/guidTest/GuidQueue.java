package guidTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class GuidQueue {

    // 기본 컨테이너 이름
    private static final String DEFAULT_CONTAINER_NAME = "OSI01";

    private final LinkedBlockingQueue<String> queue;

    // AtomicLong을 사용하여 GUID 생성 시 고유한 숫자 값을 증가시킴
    private static final AtomicLong atomicCounter = new AtomicLong(0);

    // 싱글톤 인스턴스 (volatile 사용하여 멀티스레드 환경에서 안전하게 관리)
    private static volatile GuidQueue instance;

    // private 생성자로 외부 인스턴스 생성을 방지 (싱글톤 패턴)
    private GuidQueue(int queueSize) {
        this.queue = new LinkedBlockingQueue<>(queueSize); // 큐의 크기 지정
    }

    // 싱글톤 인스턴스를 반환하는 메서드 (Double-Checked Locking 기법 사용)
    public static GuidQueue getInstance(int queueSize) {
        if (instance == null) {
            synchronized (GuidQueue.class) {
                if (instance == null) {
                    instance = new GuidQueue(queueSize); // 큐 크기를 인자로 받아 싱글톤 인스턴스 생성
                }
            }
        }
        return instance;
    }

    // GUID 생성 및 큐에 추가하는 메서드
    public void makeGuidSeq() throws InterruptedException {
        // 큐에 GUID가 가득 찰 때까지 반복하여 생성
        do {
            String guid = getPidSeqGUID(); // PID와 고유 숫자 조합하여 GUID 생성
            queue.put(guid); // 큐에 GUID 추가
        } while (queue.remainingCapacity() > 0); // 큐가 가득 차면 종료
        System.out.println("모든 GUID 생산 완료");
    }

    // PID와 고유 숫자를 조합하여 GUID 생성
    public String getPidSeqGUID() {
        return String.format("%s:%s", getThreadPID(), generateAtomicGUID()).toUpperCase();
    }

    // 환경변수에서 컨테이너 이름을 읽어오는 메소드, 없으면 기본값 사용
    @SuppressWarnings("unused")
    private static String getContainerName() {
        return Optional.ofNullable(System.getenv("CONTAINER_NAME"))
                .filter(name -> !name.isEmpty())
                .orElse(getThreadPID());
    }    

    // 현재 쓰레드의 PID와 ID를 조합하여 반환
    @SuppressWarnings("deprecation")
    private static String getThreadPID() {
        return ProcessHandle.current().pid() + String.format("%02d", Thread.currentThread().getId());
    }

    // 고유 숫자 값을 생성 (7자리 숫자 제한)
    private static String generateAtomicGUID() {
        long currentValue = atomicCounter.incrementAndGet(); // 현재 값 증가

        // if (currentValue >= 10) { // 7자리 넘으면 1부터 다시 시작
        if (currentValue >= 10000000) { // 7자리 넘으면 1부터 다시 시작
            atomicCounter.set(0); // 카운터를 0으로 리셋
            currentValue = atomicCounter.incrementAndGet(); // 다시 1로 시작
        }
        return String.format("%07d", currentValue); // 7자리 숫자로 포맷
    }

    // GUID를 포함한 큐를 반환
    public LinkedBlockingQueue<String> getQueue() {
        return queue;
    }

    // GUID를 큐에서 꺼내고, 추가적인 포맷팅을 해서 반환
    public String getGUID() throws InterruptedException {
        String rawGUID = queue.take(); // 큐에서 GUID 가져오기
        return getFormatGUID(String.format("%s:%s", getCurrentDate(), getFormatGUID(rawGUID).toUpperCase()));
    }

    // 현재 날짜와 시간을 "yyyyMMddHHmmss" 형식으로 반환
    private static String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    // 생성된 GUID가 32자 이상일 경우 자르고, 그렇지 않으면 0으로 채워서 길이를 맞춤
    private static String getFormatGUID(String rawGUID) {
        return rawGUID.length() >= 32 ? rawGUID.substring(0, 32) : String.format("%-32s", rawGUID).replace(' ', '0');
    }
}
