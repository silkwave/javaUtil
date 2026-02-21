package subpub;

import java.util.List;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PubSub {

    public static void main(String[] args) {
        // 1에서 10까지의 정수를 가지는 리스트를 생성하고 이를 Publisher로 변환
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
        // pub를 받아서 각 항목을 10배씩 곱한 새로운 Publisher를 생성
        Publisher<Integer> mapPub = mapPub(pub, s -> s * 10);
        // mapPub를 받아서 각 항목을 음수로 바꾼 새로운 Publisher를 생성
        Publisher<Integer> map2Pub = mapPub(mapPub, s -> -s);
        // 최종적으로 생성된 Publisher에 logger Subscriber를 등록
        map2Pub.subscribe(logSub());
    }

    // Publisher를 받아서 각 항목에 함수를 적용하는 함수형 인터페이스를 인자로 받는 map 연산자
    private static Publisher<Integer> mapPub(Publisher<Integer> pub, Function<Integer, Integer> f) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                pub.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        sub.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer i) {
                        // 각 항목에 함수를 적용한 결과를 Subscriber에 전달
                        sub.onNext(f.apply(i));
                    }

                    @Override
                    public void onError(Throwable t) {
                        sub.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        sub.onComplete();
                    }
                });
            }
        }; 
    }

    // 각 항목을 출력하는 Subscriber를 반환하는 함수
    private static Subscriber<Integer> logSub() {
        return new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                // 구독 시작 시 메시지 출력 및 요청량 설정
                System.out.println("onSubscribe:");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer i) {
                // 다음 항목을 출력
                System.out.println("onNext:" +  i);
            }

            @Override
            public void onError(Throwable t) {
                // 에러 발생 시 메시지 출력
                System.out.println("onError:{}" +  t);
            }

            @Override
            public void onComplete() {
                // 완료 메시지 출력
                System.out.println("onComplete");
            }
        };
    }

    // 리스트를 받아서 해당 리스트의 각 항목을 발행하는 Publisher를 생성하는 함수
    private static Publisher<Integer> iterPub(final List<Integer> iter) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                sub.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        try {
                            // 요청량만큼 각 항목을 Subscriber에게 전달
                            iter.forEach(s -> sub.onNext(s));
                            // 전체 항목 발행 후 완료 메시지 전달
                            sub.onComplete();
                        } catch (Throwable t) {
                            // 에러 발생 시 에러 메시지 전달
                            sub.onError(t);
                        }
                    }

                    @Override
                    public void cancel() {
                        // 구독 취소 요청 시 아무 작업도 수행하지 않음
                    }
                });
            }
        };
    }
}
