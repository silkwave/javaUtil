package SamDbloader;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

// 데이터 처리 인터페이스
interface DataProcessor {
    void processData(String line);
}

// 파일 저장 인터페이스
interface DataSaver {
    void saveData(String data);
}

// 추상 팩토리 인터페이스
interface DataFactory {
    DataProcessor createDataProcessor();

    DataSaver createDataSaver();
}

// 구체적인 데이터 처리 클래스
class ConcreteDataProcessor implements DataProcessor {
    private final DataSaver saver;

    public ConcreteDataProcessor(DataSaver saver) {
        this.saver = saver;
    }

    @Override
    public void processData(String line) {
        try {
            TimeUnit.MILLISECONDS.sleep(SamDbLoader.DATA_PROCESSING_DELAY_MILLISECONDS_S);
            System.out.println("===============> 라인 처리 및 저장: " + line);
            saver.saveData(line); // 데이터 저장
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// 구체적인 파일 저장 클래스
class ConcreteDataSaver implements DataSaver {
    @Override
    public void saveData(String data) {
        try {
            Files.writeString(Path.of(SamDbLoader.OUTPUT_FILE_PATH), data + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// 구체적인 데이터 처리 및 파일 저장 팩토리
class ConcreteDataFactory implements DataFactory {
    @Override
    public DataProcessor createDataProcessor() {
        return new ConcreteDataProcessor(createDataSaver());
    }

    @Override
    public DataSaver createDataSaver() {
        return new ConcreteDataSaver();
    }
}

public class SamDbLoader {
    public static final int DATA_PROCESSING_DELAY_MILLISECONDS_C = 1;
    public static final int DATA_PROCESSING_DELAY_MILLISECONDS_S = 2;
    public static final String OUTPUT_FILE_PATH = "processed_output.txt";

    private final DataFactory dataFactory;

    public SamDbLoader(DataFactory dataFactory) {
        this.dataFactory = dataFactory;
    }

    // 파일을 읽어와서 옵저버블을 생성하는 메서드
    public Observable<String> readFile(String filePath) {
        return Observable.create(emitter -> {
            try {
                Files.lines(Path.of(filePath))
                        .forEach(line -> {
                            System.out.println("===============> 읽기: " + line);
                            emitter.onNext(line);
                            try {
                                TimeUnit.MILLISECONDS.sleep(DATA_PROCESSING_DELAY_MILLISECONDS_C);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                emitter.onComplete();
            } catch (IOException e) {
                emitter.onError(e);
            }
        });
    }

    // 데이터 처리를 수행하는 메서드
    public void processData(CountDownLatch latch) {

        String filePath = "logfile.txt";
        Observable<String> fileObservable = readFile(filePath);
        DataProcessor processor = dataFactory.createDataProcessor();

        fileObservable.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(processor::processData,
                        error -> {
                            System.err.println("데이터 처리 중 에러 발생: " + error.getMessage());
                            latch.countDown(); // 에러 발생 시 카운트 다운
                        },
                        () -> {
                            System.out.println("************ 데이터 처리 완료! ************");
                            latch.countDown(); // 처리 완료 시 카운트 다운
                        });
    }

    public static void main(String[] args) throws InterruptedException {

        SamDbLoader loader = new SamDbLoader(new ConcreteDataFactory());
        CountDownLatch latch = new CountDownLatch(1); // 카운트 다운 래치 초기화
        loader.processData(latch);
        latch.await(); // 파일 처리 및 데이터 처리가 완료될 때까지 대기
    }
}
