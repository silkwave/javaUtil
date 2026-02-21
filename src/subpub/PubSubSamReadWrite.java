package subpub;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Function;

public class PubSubSamReadWrite {  

    public static void main(String[] args) {
        // SAM 파일에서 로그를 읽어올 Publisher 생성
        Publisher<String> pub = createSamFilePublisher(Paths.get("syslog"));

        // ERROR로 시작하는 로그만 필터링하여 새로운 Publisher 생성
        Publisher<String> filterPub = createFilteredPublisher(pub, s -> s.contains("Starting podman-restart.service"));

        // 필터링된 로그를 처리할 Subscriber 생성
        Subscriber<String> logProcessorSub = createSamFileSubscriber();

        // 필터링된 로그를 처리하기 위해 Subscriber를 Publisher에 등록
        filterPub.subscribe(logProcessorSub);
    }
    
    // SAM 파일을 읽어와서 각 줄을 발행하는 Publisher를 생성하는 메서드
    private static Publisher<String> createSamFilePublisher(Path filePath) {
        return subscriber -> {
            try {
                Files.lines(filePath).forEach(subscriber::onNext);
                subscriber.onComplete();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        };
    }

    // 로그를 필터링하여 새로운 Publisher를 생성하는 메서드
    private static Publisher<String> createFilteredPublisher(Publisher<String> pub,  Function<String, Boolean> predicate) {
        
        return subscriber -> pub.subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                subscriber.onSubscribe(subscription);
            }

            @Override
            public void onNext(String item) {
                   
                // 주어진 조건에 따라 로그를 필터링하고 Subscriber에게 전달
                if (predicate.apply(item)) {
                    System.out.println("          createFilteredPublisher onNext: " + item);   
                    subscriber.onNext(item);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                subscriber.onError(throwable);
            }

            @Override
            public void onComplete() {
                System.out.println("createFilteredPublisher onComplete ");                
                subscriber.onComplete();
            }
        });
    }

    // SAM 파일에 로그를 적재하는 Subscriber를 생성하는 메서드
    private static Subscriber<String> createSamFileSubscriber() {
        return new Subscriber<>() {
            private final Path logFilePath = Paths.get("out.sam");

            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(10L);  /*  request  Long.MAX_VALUE */
            }

            @Override
            public void onNext(String item) {
                try {
                    // SAM 파일에 로그를 적재
                    System.out.println("createSamFileSubscriber onNext: " + item);
                    Files.writeString(logFilePath, item + System.lineSeparator(), StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND);
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
                System.out.println("createSamFileSubscriber onComplete ");
            }
        };
    }
}
