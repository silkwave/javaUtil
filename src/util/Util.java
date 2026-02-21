package util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.function.Function;

public interface Util {

	static final char hexchar[] = new String("0123456789ABCDEF").toCharArray();

	// 문자열 -> 헥사 문자열 변환
	public static String StringToHexDecimal(String lwString) {

		byte[] bts = lwString.getBytes();

		StringBuffer sb = new StringBuffer();

		for (byte bt : bts) {
			sb.append(hexchar[(bt >> 4) & 0x0F]);
			sb.append(hexchar[(bt) & 0x0F]);
		}

		return sb.toString().toUpperCase();
	}

	// 헥사 문자열 -> 문자열 변환
	public static String HexDecimalToString(String hex) {
		hex = hex.replace(" ", ""); // 공백 제거
		int length = hex.length();
		byte[] byteArray = new byte[length / 2];
		for (int i = 0; i < length; i += 2) {
			byteArray[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
		}
		return new String(byteArray);
	}

	public static String makePhoneNumber(String phoneNumber) {

		String regEx = "(\\d{3})(\\d{3,4})(\\d{4})";

		if (!Pattern.matches(regEx, phoneNumber))
			return phoneNumber;

		return phoneNumber.replace(regEx, "$1-$2-$3");
	}

	public static String showMemory() {

		Runtime rt = Runtime.getRuntime();

		DecimalFormat df = new DecimalFormat("###,###,###.##");

		long max = rt.maxMemory();
		long total = rt.totalMemory();
		long free = rt.freeMemory();

		return ("Max : " + df.format(max) + " Total : " + df.format(total) + " Free : " + df.format(free));

	}

	public static String ReplaceAt(String lwString, int sidx, int edix, String lwRepString) {

		StringBuffer sb = new StringBuffer(lwString);

		return sb.replace(sidx, edix, lwRepString).toString();

	}

	public static String stringRepeat(String lwString, int count) {

		StringBuffer sb = new StringBuffer(count);

		for (int lw = 0; lw < count; lw++) {
			sb.append(lwString);
		}

		return sb.toString();

	}

	/**
	 * 
	 * @param log
	 */
	public static void log(Object log) {

		StringBuffer sb = new StringBuffer();

		sb.append("(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":");
		sb.append(String.format("%04d", Thread.currentThread().getStackTrace()[2].getLineNumber()) + ")  ");
		sb.append(log);

		System.out.println(sb);

	}

	public static String addDay(String indate, long day) {

		LocalDate date = LocalDate.parse(indate, DateTimeFormatter.BASIC_ISO_DATE);

		LocalDate plusDays = date.plusDays(day); // (오늘 + 1일) = 내일

		return plusDays.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	}

	public static String getDay() {

		return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

	}

	public static String byteSubstr(String str, int byteStart, int byteEnd, String encoding) {

		String result = null;
		try {
			byte[] bytes = str.getBytes(encoding);
			printByteArrayInHex(bytes);
			int actualStart = Math.max(0, Math.min(byteStart, bytes.length));
			int actualEnd = Math.max(actualStart, Math.min(byteEnd, bytes.length));
			byte[] truncatedBytes = Arrays.copyOfRange(bytes, actualStart, actualEnd);
			printByteArrayInHex(truncatedBytes);
			result = new String(truncatedBytes, encoding);

		} catch (Exception e) {
			throw new RuntimeException("문자열 오류");
		}
		return result;

	}

	public static void printByteArrayInHex(byte[] bytes) {

		for (byte b : bytes) {
			System.out.print(String.format("0x%02X ", b));
		}
		System.out.println();
	}

	// 로그 메시지를 포맷팅하여 반환하는 람다 표현식
	public static Function<String, String> logFormatter = message -> {
		StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
		return String.format("(%s:%d) [%s] %s",
				ste.getFileName(),
				ste.getLineNumber(),
				ste.getMethodName(),
				message);
	};

	/**
	 * 바이트 배열에서 특정 바이트를 제거(strip)합니다.
	 * @param byteArray 원본 바이트 배열
	 * @param byteToStrip 제거할 바이트
	 * @return 특정 바이트가 제거된 새로운 바이트 배열
	 */
	public static byte[] stripByte(byte[] byteArray, byte byteToStrip) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		for (byte b : byteArray) {
			if (b != byteToStrip) {
				outputStream.write(b);
			}
		}
		return outputStream.toByteArray();
	}

	/**
	 * 바이트 배열에서 유효하지 않은 바이트(0xFF)를 필터링하고, 지정된 문자셋을 사용해 안전하게 문자열로 변환합니다.
	 * @param byteArray 원본 바이트 배열
	 * @param charsetName 사용할 문자셋 이름 (예: "MS949", "UTF-8")
	 * @return 변환된 문자열
	 */
	public static String toSafeString(byte[] byteArray, String charsetName) {
		byte[] filteredByteArray = stripByte(byteArray, (byte) 0xFF);
		Charset charset = Charset.forName(charsetName);
		return new String(filteredByteArray, charset);
	}

	/**
	 * byte를 char로 변환한 후 String으로 반환합니다.
	 * @param b 변환할 byte 값
	 * @return 변환된 최종 문자열
	 */
	public static String byteToCharString(byte b) {
		return String.valueOf((char) b);
	}

}
