package guidTest;

public class GuidProducer implements Runnable {
    // GuidQueue 인스턴스를 전역적으로 사용하기 위해 static으로 선언
    private static GuidQueue util; // GuidQueue를 전역적으로 관리

    // GuidProducer 생성자: GuidQueue 인스턴스를 받아옴
    public GuidProducer(GuidQueue util) {
        GuidProducer.util = util; // 전역 객체로 사용하기 위해 static 변수에 할당
    }

    @Override
    public void run() {
        try {
            // GUID를 생성하여 큐에 추가하는 작업 수행
            util.makeGuidSeq();  // GUID 생성 및 큐에 추가 (큐가 가득 찰 때까지 반복)
        } catch (InterruptedException e) {
            e.printStackTrace(); // InterruptedException이 발생한 경우 스택 트레이스 출력
        }
    }
}
