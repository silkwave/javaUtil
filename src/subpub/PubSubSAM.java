package subpub;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Function;

public class PubSubSAM {

    public static void main(String[] args) {
        // 로그 항목을 포함하는 Publisher 생성
        Publisher<String> pub = iterPub(List.of(
                "INFO: This is a log message.",
                "ERROR: 1 An error occurred.",
                "ERROR: 2 An error occurred.",
                "ERROR: 3 An error occurred.",
                "ERROR: 4 An error occurred.",
                "WARN: Warning message.",
                "INFO: Another informational message."
        ));

        // ERROR로 시작하는 로그만 필터링하여 새로운 Publisher 생성
        Publisher<String> filterPub = filterPub(pub, s -> s.startsWith("ERROR"));

        // SAM 파일에 로그를 적재할 Subscriber 생성
        Subscriber<String> samFileSub = samFileSub();

        // 필터링된 로그를 SAM 파일에 적재하기 위해 Subscriber를 Publisher에 등록
        filterPub.subscribe(samFileSub);
    }

    // 로그를 필터링하여 새로운 Publisher를 생성하는 메서드
    private static Publisher<String> filterPub(Publisher<String> pub, Function<String, Boolean> predicate) {
        return subscriber -> pub.subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                subscriber.onSubscribe(subscription);
            }

            @Override
            public void onNext(String item) {
                // 주어진 조건에 따라 로그를 필터링하고 Subscriber에게 전달
                if (predicate.apply(item)) {
                    subscriber.onNext(item);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                subscriber.onError(throwable);
            }

            @Override
            public void onComplete() {
                subscriber.onComplete();
            }
        });
    }

    // 주어진 로그 항목들을 포함하는 Publisher를 생성하는 메서드
    private static Publisher<String> iterPub(final List<String> logEntries) {
        return subscriber -> subscriber.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                try {
                    // 모든 로그 항목을 Subscriber에게 전달하고 완료를 알림
                    for (String entry : logEntries) {
                        subscriber.onNext(entry);
                    }
                    subscriber.onComplete();
                } catch (Throwable t) {
                    subscriber.onError(t);
                }
            }

            @Override
            public void cancel() {
                // 정리 작업 필요 없음
            }
        });
    }

    // SAM 파일에 로그를 적재하는 Subscriber를 생성하는 메서드
    private static Subscriber<String> samFileSub() {
        return new Subscriber<>() {
            private final Path logFilePath = Paths.get("log.sam");

            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(String item) {
                try {
                    // SAM 파일에 로그를 적재
                // 필터링된 로그를 처리하는 작업 수행 (여기서는 간단히 콘솔에 출력)
                System.out.println("Processing filtered log: " + item);
                                    
                    Files.writeString(logFilePath, item + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                // 완료 시 동작 필요 없음
            }
        };
    }
}
