package rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class RxJavaLogFileExample {

  private static final String LOG_FILE_NAME = "logfile.txt";
  private static final int LOG_INTERVAL_SECONDS = 1;
  private static final int WAIT_TIME_BEFORE_EXIT_MS = 10000;

  public static void main(String[] args) {
    // 로그 스트림 생성
    Observable<String> logStream = createLogStream();
    // 로그 스트림을 구독하여 파일에 기록
    Disposable disposable = logStream.subscribe(
        RxJavaLogFileExample::writeToFile,
        Throwable::printStackTrace,
        () -> System.out.println("로그 스트림이 완료되었습니다."));
    // 일정 시간 대기 후 프로그램 종료
    waitForExit(disposable);
  }

  // 로그 생성 Observable 생성
  private static Observable<String> createLogStream() {
    return Observable.interval(LOG_INTERVAL_SECONDS, TimeUnit.SECONDS)
        .map(tick -> createLogMessage("DEBUG", "This is a debug message #" + tick))
        .takeUntil(tick -> Long.parseLong(tick.substring(tick.lastIndexOf("#") + 1)) == 10L);
  }

  // 로그 메시지 생성
  private static String createLogMessage(String level, String message) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    String timestamp = formatter.format(now);
    return timestamp + " [" + level + "] " + message;
  }

  // 로그를 파일에 쓰는 메서드
  private static void writeToFile(String log) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_NAME, true))) {
      writer.write(log + "\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // 프로그램이 종료될 때까지 대기
  private static void waitForExit(Disposable disposable) {
    try {
      Thread.sleep(WAIT_TIME_BEFORE_EXIT_MS);
      disposable.dispose(); // 구독 종료
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
