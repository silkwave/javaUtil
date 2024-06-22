package util;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public interface Util {

	static final char hexchar[] = new String("0123456789ABCDEF").toCharArray();

	public static String StringToHexDecimal(String lwString) {

		byte[] bts = lwString.getBytes();

		StringBuffer sb = new StringBuffer();

		for (byte bt : bts) {
			sb.append(hexchar[(bt >> 4) & 0x0F]);
			sb.append(hexchar[(bt) & 0x0F]);
		}

		return sb.toString().toUpperCase();
	}

	public static String HexDecimalToString(String lwString) {

		int len = lwString.length() / 2;

		byte[] bts = new byte[len];

		for (int lw = 0; lw < len; lw++) {
			bts[lw] = HexDeciamlToByte(lwString.substring(2 * lw, (2 * lw) + 2));
		}

		return new String(bts);

	}

	private static byte HexDeciamlToByte(String lwString) {

		return Integer.valueOf(lwString, 16).byteValue();

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

}
