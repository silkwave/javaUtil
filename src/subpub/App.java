package subpub;

import java.util.Iterator;
import java.util.Arrays;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;

public class App {
public static void main(String[] args) {
    // 데이터 100만 개를 생성
    int[] numbers = new int[20];
    for (int i = 0; i < numbers.length; i++) {
        numbers[i] = i + 1;
    }

    // Publisher 구현
    Publisher<Integer> publisher = new Publisher<Integer>() {
        @Override
        public void subscribe(Subscriber<? super Integer> subscriber) {
            subscriber.onSubscribe(new Flow.Subscription() {
                private final Iterator<Integer> iterator = Arrays.stream(numbers).iterator();
                private long requested;

                @Override
                public void request(long n) {
                    synchronized (this) {
                        requested += n;
                    }

                    while (requested > 0) {
                        if (iterator.hasNext()) {
                            Integer next = iterator.next();
                            subscriber.onNext(next);
                            requested--;

                            try {
                                Thread.sleep(100); // 1초씩 딜레이
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                subscriber.onError(e);
                                return;
                            }
                        } else {
                            subscriber.onComplete();
                            break;
                        }
                    }
                }

                @Override
                public void cancel() {
                    // 구현 생략
                }
            });
        }
    };

    // Subscriber 구현
    Subscriber<Integer> subscriber = new Subscriber<Integer>() {
        private Flow.Subscription subscription;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            System.out.println("onSubscribe");
            this.subscription = subscription;
            subscription.request(1); // 처음 1개 요청
        }

        @Override
        public void onNext(Integer item) {
            System.out.println("onNext: " + item);
            subscription.request(1); // 1개씩 추가 요청
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("onError: " + throwable.getMessage());
        }

        @Override
        public void onComplete() {
            System.out.println("onComplete");
        }
    };

    // Publisher와 Subscriber 연결
    publisher.subscribe(subscriber);
}

}
