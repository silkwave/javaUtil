package rxjava;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SamDbLoaderFlux_single {
    private static final int DATA_PROCESSING_DELAY_MILLISECONDS_C = 1;
    private static final int DATA_PROCESSING_DELAY_MILLISECONDS_S = 2;
    private static final String OUTPUT_FILE_PATH = "processed_output.txt";

    // 파일을 읽어오는 Flux를 생성하는 메서드
    private Flux<String> readFile(String filePath) {
        return Flux.using(
                () -> Files.lines(Paths.get(filePath)),
                Flux::fromStream,
                stream -> {
                    try {
                        stream.close();
                    } catch (Exception ignore) {
                    }
                })
                .delayElements(Duration.ofMillis(DATA_PROCESSING_DELAY_MILLISECONDS_C))
                .subscribeOn(Schedulers.boundedElastic());
    }

    // 데이터를 처리하는 Flux를 생성하는 메서드
    private Flux<String> processData(Flux<String> lines) {
        return lines.parallel()
                .runOn(Schedulers.parallel())
                .doOnNext(this::processDataInternal)
                .doOnError(error -> System.err.println("데이터 처리 중 에러 발생: " + error.getMessage()))
                .sequential()
                .doOnComplete(() -> System.out.println("************ 데이터 처리 완료! ************"));
    }

    // 데이터를 내부적으로 처리하는 메서드
    private void processDataInternal(String line) {
        try {
            TimeUnit.MILLISECONDS.sleep(DATA_PROCESSING_DELAY_MILLISECONDS_S);
            saveToFile(line);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // System.out.println("===============> 라인 처리 및 저장: " + line);
    }

    // 데이터를 파일에 저장하는 메서드
    private void saveToFile(String data) {
        try {
            Files.write(Paths.get(OUTPUT_FILE_PATH), (data + System.lineSeparator()).getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) throws InterruptedException {
        String filePath = "logfile.txt";
        SamDbLoaderFlux_single loader = new SamDbLoaderFlux_single(); // 수정: SamDbLoaderFlux_single 인스턴스 생성
        Flux<String> fileFlux = loader.readFile(filePath).log(); // 수정: SamDbLoaderFlux_single 인스턴스의 메서드 호출
        Flux<String> processedFlux = loader.processData(fileFlux); // 수정: SamDbLoaderFlux_single 인스턴스의 메서드 호출
        CountDownLatch latch = new CountDownLatch(1);
        processedFlux.subscribe(
                onNext -> {
                }, // onNext callback 
                throwable -> {
                }, // onError callback
                latch::countDown // onComplete callback
        ); // Start the subscription
        latch.await(); // Wait until latch reaches 0
    }
}
