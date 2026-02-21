package datetest;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

public class LocalDateTest {

    // 날짜와 시간 포맷터
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        printTodayAndNow();
        calculatePeriod();
        calculateTimeDuration();
        formatExamples();
        displayDayAndMonthNames();
        findNextSaturday();
    }

    // 오늘 날짜와 현재 시간 출력
    private static void printTodayAndNow() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        System.out.println("오늘 날짜: " + today.format(DATE_FMT));
        System.out.println("현재 시간: " + now.format(TIME_FMT));
    }

    // 특정 기간 계산
    private static void calculatePeriod() {
        LocalDate startDate = LocalDate.of(1950, 6, 25);
        LocalDate endDate = LocalDate.of(1953, 7, 27);
        Period period = Period.between(startDate, endDate);
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        System.out.println("\n1950년 6월 25일부터 1953년 7월 27일까지:");
        System.out.println("시작 날짜: " + startDate.format(DATE_FMT));
        System.out.println("종료 날짜: " + endDate.format(DATE_FMT));
        System.out.println("기간: " + period.getYears() + "년 " + period.getMonths() + "개월 " + period.getDays() + "일");
        System.out.println("일수 차이: " + daysBetween + "일");
    }

    // 시간 간격 계산
    private static void calculateTimeDuration() {
        LocalTime startTime = LocalTime.of(10, 50, 10);
        LocalTime endTime = LocalTime.of(19, 0, 20);
        Duration duration = Duration.between(startTime, endTime);
        Duration additionalDuration = Duration.ofHours(1).plusMinutes(30);
        Duration totalDuration = duration.plus(additionalDuration);

        System.out.println("\n시간 계산:");
        System.out.println("시작 시간: " + startTime.format(TIME_FMT));
        System.out.println("종료 시간: " + endTime.format(TIME_FMT));
        System.out.println("시간 차이: " + formatDuration(duration));
        System.out.println("추가된 시간 (1시간 30분): " + formatDuration(additionalDuration));
        System.out.println("총 시간 차이: " + formatDuration(totalDuration));
    }

    // 시간 간격 포맷팅
    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return hours + "시간 " + minutes + "분 " + seconds + "초";
    }

    // 날짜와 시간 포맷팅 예시
    private static void formatExamples() {
        LocalDate exampleDate = LocalDate.of(2020, 12, 12);
        LocalDateTime now = LocalDateTime.now();

        System.out.println("\n포맷팅 예시:");
        System.out.println("포맷팅된 날짜: " + exampleDate.format(DATE_FMT));
        System.out.println("포맷팅된 현재 시간: " + now.format(DATE_TIME_FMT));
    }

    // 요일과 월 이름 출력
    private static void displayDayAndMonthNames() {
        System.out.println("\n요일 및 월 이름:");
        System.out.println("요일 (영어): " + DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        System.out.println("요일 (한국어): " + DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.KOREAN));
        System.out.println("2월 (영어): " + Month.FEBRUARY.getDisplayName(TextStyle.FULL, Locale.US));
        System.out.println("2월 (한국어): " + Month.FEBRUARY.getDisplayName(TextStyle.FULL, Locale.KOREA));
    }

    // 다음 토요일 찾기
    private static void findNextSaturday() {
        LocalDate today = LocalDate.now();
        LocalDate nextSaturday = today.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));

        System.out.println("\n다음 토요일 날짜: " + nextSaturday.format(DATE_FMT));
    }
}
