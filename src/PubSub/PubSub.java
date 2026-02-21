package PubSub;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class PubSub {
    public static void main(String[] args) throws InterruptedException {
        Map<Integer, Integer> dataMap = createDataMap(100); // 10개의 정수를 가지는 HashMap 생성
        ExecutorService es = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(1); // 작업 완료를 기다리는 CountDownLatch

        Publisher<Integer> publisher = createPublisher(dataMap, es, latch);
        Subscriber<Integer> subscriber = createSubscriber(latch);

        publisher.subscribe(subscriber);

        latch.await(); // Publisher가 작업을 완료할 때까지 대기
        es.shutdown();
    }

    // 주어진 크기만큼의 정수를 가지는 HashMap을 생성하는 메서드
    private static Map<Integer, Integer> createDataMap(int size) {
        Map<Integer, Integer> dataMap = new HashMap<>();
        for (int i = 1; i <= size; i++) {
            dataMap.put(i, i);
        }
        return dataMap;
    }

    // Publisher를 생성하는 메서드
    private static Publisher<Integer> createPublisher(Map<Integer, Integer> dataMap, ExecutorService executorService,
                                                      CountDownLatch latch) {
        return subscriber -> {
            subscriber.onSubscribe(new Subscription() {
                private Iterator<Integer> iterator = dataMap.values().iterator();
                private boolean completed = false;

                @Override
                public void request(long n) {
                    executorService.execute(() -> {
                        try {
                            long count = 0;
                            while (count < n && iterator.hasNext()) {
                                subscriber.onNext(iterator.next());
                                count++;
                            }
                            if (!iterator.hasNext() && !completed) {
                                subscriber.onComplete();
                                completed = true;
                                latch.countDown(); // 작업 완료를 알림
                            }
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    });
                }

                @Override
                public void cancel() {
                    // 필요한 경우 구독을 취소하는 로직을 여기에 구현
                }
            });
        };
    }

    // Subscriber를 생성하는 메서드
    private static Subscriber<Integer> createSubscriber(CountDownLatch latch) {
        return new Subscriber<Integer>() {
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(1); // 초기에 1개의 아이템을 요청
            }

            @Override
            public void onNext(Integer item) {
                System.out.println(Thread.currentThread().getName() + " onNext: " + item);
                subscription.request(1); // 다음 아이템 요청

                try {
                    // 다음 아이템을 요청하기 전에 50밀리초의 딜레이를 추가
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError");
                latch.countDown(); // 에러 발생 시 작업 완료를 알림
            }

            @Override
            public void onComplete() {
                System.out.println(Thread.currentThread().getName() + " onComplete");
            }
        };
    }
}
