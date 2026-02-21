package guidTest;

public class BizProc {

    // GuidTest에서 선언된 static util을 바로 사용하여 GUID를 처리
    public void processGUIDs() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            // GuidTest 클래스의 static guidQueue 객체를 직접 사용하여 GUID를 가져옴
            String GUID = GuidTest.guidQueue.getGUID(); // GuidTest의 guidQueue 객체에서 GUID를 가져옴
            System.out.println(Thread.currentThread().getName() + " GUID: " + GUID); // 현재 스레드 이름과 함께 생성된 GUID 출력
        }
    }
}
