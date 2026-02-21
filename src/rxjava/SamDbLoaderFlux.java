package rxjava;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.BaseStream;


import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class SamDbLoaderFlux {
    private static final int DATA_PROCESSING_DELAY_MILLISECONDS_C = 1;
    private static final int DATA_PROCESSING_DELAY_MILLISECONDS_S = 2;
    private static final String FILE_PATH = "logfile.txt"; // 파일 경로를 상수로 이동
    private static final String OUTPUT_FILE_PATH = "processed_logfile_"; // 처리된 데이터를 저장할 파일 경로
    private static final int CONSUMER_THREADS = 2; // 컨슈머 스레드 수

    private Flux<String> readFile(String filePath) {
        return Flux.using(
                () -> Files.lines(Paths.get(filePath)),
                Flux::fromStream,
                BaseStream::close)
                .delayElements(Duration.ofMillis(DATA_PROCESSING_DELAY_MILLISECONDS_C));
    }

    private void processData(Flux<String> lines, CountDownLatch latch) {
        ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_THREADS);

        lines.subscribeOn(Schedulers.boundedElastic())
                .subscribe(line -> executor.submit(() -> {
                            processDataInternal(line, latch.getCount());
                            latch.countDown();
                        }),
                        error -> System.err.println("데이터 처리 중 에러 발생: " + error.getMessage()),
                        () -> {
                            try {
                                latch.await(); // 모든 작업이 완료될 때까지 대기
                                executor.shutdown(); // 스레드 풀 종료
                                System.out.println("데이터 처리 완료!");
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        });
    }

    private void processDataInternal(String line, long threadId) {
        try {

           // 각 줄을 처리하는 로직을 구현 (예: 파일에 저장)


            saveProcessedLine(line, threadId);

             System.out.println("===============> 라인 처리 및 저장: " + line);            

            Thread.sleep(DATA_PROCESSING_DELAY_MILLISECONDS_S);            

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    private synchronized void saveProcessedLine(String line, long threadId) {
        String fileName = OUTPUT_FILE_PATH + threadId + ".txt";
        try {
            Files.write(Paths.get(fileName), (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("파일 저장 중 오류 발생: " + e.getMessage());
        }
    }
 
    public static void main(String[] args) throws InterruptedException {
        SamDbLoaderFlux loader = new SamDbLoaderFlux();
        Flux<String> fileFlux = loader.readFile(FILE_PATH).log(); // 상수 사용
        CountDownLatch latch = new CountDownLatch(CONSUMER_THREADS);
        loader.processData(fileFlux, latch);
        latch.await(); // 데이터 처리가 완료될 때까지 대기
        System.out.println("프로그램 종료");
    }
}
