package rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SamDbLoader {
    private static final int DATA_PROCESSING_DELAY_MILLISECONDS_C = 1;
    private static final int DATA_PROCESSING_DELAY_MILLISECONDS_S = 2;
    private static final String OUTPUT_FILE_PATH = "processed_output.txt";

    // 파일을 읽어와서 옵저버블을 생성하는 메서드
    public Observable<String> readFile(String filePath) {
        return Observable.create(emitter -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null && !emitter.isDisposed()) {
                    System.out.println("발행된 라인: " + line);
                    emitter.onNext(line);
                    try {
                        TimeUnit.MILLISECONDS.sleep(DATA_PROCESSING_DELAY_MILLISECONDS_C);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    // 데이터 처리 및 저장을 수행하는 Observable을 반환하는 메서드
    public Observable<String> processData(Observable<String> lines) {
        return lines.observeOn(Schedulers.io())
                .doOnNext(this::processDataInternal)
                .doOnError(error -> System.err.println("데이터 처리 중 에러 발생: " + error.getMessage()))
                .doOnComplete(() -> System.out.println("************ 데이터 처리 완료! ************"));
    }

    // 데이터 처리 및 저장을 내부적으로 수행하는 메서드
    private void processDataInternal(String line) {
        try {
            TimeUnit.MILLISECONDS.sleep(DATA_PROCESSING_DELAY_MILLISECONDS_S);
            // 파일에 라인 저장
            saveToFile(line);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("===============> 라인 처리 및 저장: " + line);
    }

    // 파일에 데이터를 저장하는 메서드
    private void saveToFile(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) throws InterruptedException {
        String filePath = "logfile.txt";
        SamDbLoader loader = new SamDbLoader();
        Observable<String> fileObservable = loader.readFile(filePath);
        Observable<String> processedObservable = loader.processData(fileObservable);
        CountDownLatch latch = new CountDownLatch(1);
        processedObservable.subscribe(
                onNext -> {
                }, // onNext 콜백
                throwable -> {
                }, // onError 콜백
                latch::countDown // onComplete 콜백
        ); // 실제 구독을 시작합니다.
        System.out.println(latch.toString());
        latch.await(); // latch가 0이 될 때까지 대기합니다.
        System.out.println(latch.toString());
    }
}