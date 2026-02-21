GuidQueue util = GuidQueue.getInstance(5); // 큐 크기 5
Thread producerThread = new Thread(() -> {
    try {
        util.makeGuidSeq(); // GUID 생산
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
});
producerThread.start();

ExecutorService executor = Executors.newFixedThreadPool(3); // 소비자 스레드 풀 생성
for (int i = 0; i < 3; i++) {
    executor.submit(() -> {
        try {
            System.out.println("소비된 GUID: " + util.getGUID()); // GUID 소비
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    });
}

producerThread.join(); // 생산자 스레드가 끝날 때까지 대기
executor.shutdown(); // 소비자 스레드 풀 종료
executor.awaitTermination(10, TimeUnit.SECONDS); // 소비자들이 작업을 완료할 때까지 대기
