package test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Locale;

public class localDateTest {

	public static void main(String[] args) {
		LocalDate.now(); // 오늘

		LocalDateTime.now(); // 지금

		LocalDate.of(2015, 4, 17); // 2015년4월17일
		LocalDateTime.of(2015, 4, 17, 23, 23, 50); // 2015년4월17일23시23분50초
		Year.of(2015).atMonth(3).atDay(4).atTime(10, 30).format(DateTimeFormatter.ofPattern("yyMMdd")); // 2015년3월4일

		Period.ofYears(2); // 2년간(P2Y)
		Period.ofMonths(5); // 5개월간(P5M)
		Period.ofWeeks(3); // 3주간(P21D)
		Period.ofDays(20); // 20일간(P20D)

		Duration.ofDays(2); // 48시간(PT48H)
		Duration.ofHours(8); // 8시간(PT8H)
		Duration.ofMinutes(10); // 10분간(PT10M)
		Duration.ofSeconds(30); // 30초간(PT30S)
		// 날짜 + 기간 = 날짜

		LocalTime.of(9, 0, 0).plus(Duration.ofMinutes(10)); // (9시 + 10분간) =
		// 9시10분
		LocalDate.of(2015, 5, 15).plus(Period.ofDays(1)); // (2015년5월15일 + 1일간)
		// = 2015년5월16일
		LocalDateTime.of(2015, 4, 17, 23, 47, 5).minus(Period.ofWeeks(3)); // (2015년4월17일
		// 23시47분05초
		// -
		// 3주간)
		// =
		// 2015년3월27일
		// 23시47분05초
		LocalDate.now().plusDays(1); // (오늘 + 1일) = 내일
		LocalTime.now().minusHours(3); // (지금 - 3시간) = 3시간 전
		// 날짜 - 날짜 = 기간

		Period.between(LocalDate.of(1950, 6, 25), LocalDate.of(1953, 7, 27)); // (1953년7월27일
		// -
		// 1950년6월25일)
		// =
		// 3년1개월2일간(P3Y1M2D)
		Period.between(LocalDate.of(1950, 6, 25), LocalDate.of(1953, 7, 27)).getDays(); // 3년1개월2일간 => 2일간
		LocalDate.of(1950, 6, 25).until(LocalDate.of(1953, 7, 27), ChronoUnit.DAYS); // 3년1개월2일간 => 1128일간
		ChronoUnit.DAYS.between(LocalDate.of(1950, 6, 25), LocalDate.of(1953, 7, 27)); // 3년1개월2일간 => 1128일간

		Duration.between(LocalTime.of(10, 50), LocalTime.of(19, 0)); // (19시00분00초
		// -
		// 10시50분00초)
		// =
		// 8시간10분간(PT8H10M)
		Duration.between(LocalDateTime.of(2015, 1, 1, 0, 0), LocalDateTime.of(2016, 1, 1, 0, 0)).toDays(); // 365일간
		ChronoUnit.YEARS.between(LocalDate.of(2015, 5, 5), LocalDate.of(2017, 2, 1)); // 1년간
		// 날짜 변환하기

		// LocalDate -> String

		LocalDate.of(2020, 12, 12).format(DateTimeFormatter.BASIC_ISO_DATE); // 20201212
		// LocalDateTime -> String

		LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // 2015-04-18
		// 00:42:24
		// LocalDateTime -> java.util.Date

		Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()); // Sat
		// Apr
		// 18
		// 01:00:30
		// KST
		// 2015
		// LocalDate -> java.sql.Date

		// Date.valueOf(LocalDate.of(2015, 5, 5)); // 2015-05-05
		// LocalDateTime -> java.sql.Timestamp

		// Timestamp.valueOf(LocalDateTime.now()); // 2015-04-18 01:06:55.323
		// String -> LocalDate

		LocalDate.parse("2002-05-09"); // 2002-05-09
		LocalDate.parse("20081004", DateTimeFormatter.BASIC_ISO_DATE); // 2008-10-04
		// String -> LocalDateTime

		LocalDateTime.parse("2007-12-03T10:15:30"); // 2007-12-03T10:15:30
		LocalDateTime.parse("2010-11-25 12:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // 2010-11-25T12:30
		// java.util.Date -> LocalDateTime

		// LocalDateTime.ofInstant(new Date().toInstant(),
		// ZoneId.systemDefault()); // 2015-04-18T01:16:46.755
		// java.sql.Date -> LocalDate

		// new Date(System.currentTimeMillis()).toLocalDate(); // 2015-04-18
		// java.sql.Timestamp -> LocalDateTime

		// new Timestamp(System.currentTimeMillis()).toLocalDateTime(); //
		// 2015-04-18T01:20:07.364
		// LocalDateTime -> LocalDate

		LocalDate.from(LocalDateTime.now()); // 2015-04-18
		// LocalDate -> LocalDateTime

		LocalDate.now().atTime(2, 30); // 2015-04-18T02:30
		// 요일로 날짜 가져오기

		LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY)); // 다음
		// 토요일
		LocalDate.of(2016, 5, 1).with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.SUNDAY)); // 2016년5월
		// 세번째
		// 일요일
		LocalDate.of(2015, 7, 1).with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)); // 2015년7월
		// 첫번째
		// 월요일
		// 언어별 출력

		DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.ENGLISH); // Monday
		DayOfWeek.MONDAY.getDisplayName(TextStyle.NARROW, Locale.ENGLISH); // M
		DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT, Locale.ENGLISH); // Mon

		DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.KOREAN); // 월요일
		DayOfWeek.MONDAY.getDisplayName(TextStyle.NARROW, Locale.KOREAN); // 월
		DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT, Locale.KOREAN); // 월

		Month.FEBRUARY.getDisplayName(TextStyle.FULL, Locale.US); // February
		Month.FEBRUARY.getDisplayName(TextStyle.FULL, Locale.KOREA); // 2월
	}

}
