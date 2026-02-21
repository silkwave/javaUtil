package guidTest;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GuidConsumer implements Runnable {
    private final GuidQueue util; // GUID 생성 큐
    // static으로 선언하여 모든 인스턴스가 공유할 수 있도록 만든 전역 GUID 집합
    private static final Set<String> globalGuidSet = ConcurrentHashMap.newKeySet(); // 스레드 안전한 집합 (중복 GUID 체크)

    // GuidConsumer 생성자: 큐를 받아옵니다
    public GuidConsumer(GuidQueue util) {
        this.util = util; // GUID 큐 인스턴스 할당
    }

    @Override
    public void run() {
        try {
            // 큐에서 GUID를 가져옴 (큐가 비어 있을 경우 대기)
            String guid = util.getGUID(); 

            // 큐에서 가져온 GUID가 이미 처리된 GUID 집합에 있는지 확인
            if (!globalGuidSet.add(guid)) {  // 집합에 GUID를 추가하면서 중복을 확인
                System.out.println("중복 GUID 발견: " + guid); // 중복 GUID 발견 시 출력
            } else {
                // System.out.println("GUID 소비: " + guid); // GUID 소비 시 출력
            }

            // 일정 시간 동안 대기 (1초 대기)
            Thread.sleep(100);  // 1000ms = 1초 대기
        } catch (InterruptedException e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스 출력
        }
    }
}
