package utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.*;
import java.sql.Time;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.*;

//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;

/**
 * @author Suthindran G Rao Company : Raos Infosoft Join Pvt. Ltd.
 */
public class Utils {

	public static final String OLDER_THAN_180 = "> 180 days";

	public final static String YES_STRING = "Yes";

	public final static String NO_STRING = "No";

	private final static Random randomGenerator = new Random();

	private static byte[] ENCRYPTION_KEY = new byte[] { 0x11, 0x01, 0x02, 0x03,
			0x03, 0x25, 0x06, 0x07, 0x18, 0x19, 0x0d, 0x0b, 0x0c, 0x0a, 0x4e,
			0x0f, 0x56, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x11 };

	public static String getDateTimeAsString() {
		Date date = new Date();
		return "_" + date.getDay() + "_" + date.getMonth() + "_"
				+ (1900 + date.getYear()) + "_" + date.getHours() + "_"
				+ date.getMinutes() + "_" + date.getSeconds();
	}

	public static String truncateString(String str, int length) {
		if (str == null)
			return null;
		if (str.length() <= length)
			return str;
		return str.substring(0, length) + "...";
	}

	public static boolean isNullOrEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static boolean isNumber(String str) {
		if (str == null || str.length() == 0)
			return false;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (!Character.isDigit(ch))
				return false;
		}
		return true;
	}

	public static boolean isValidTimeString(String str) {
		if (str == null)
			return false;
		StringTokenizer st = new StringTokenizer(str, ":");
		int hours = -1;
		int minutes = -1;
		if (st.countTokens() == 2) {
			try {
				hours = Integer.parseInt(st.nextToken());
				minutes = Integer.parseInt(st.nextToken());
				if (hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59)
					return true;
			} catch (NumberFormatException nfe) {
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public static Time toSqlTime(String str) {
		if (!isValidTimeString(str))
			return null;
		StringTokenizer st = new StringTokenizer(str, ":");
		int hours = -1;
		int minutes = -1;
		if (st.countTokens() == 2) {
			try {
				hours = Integer.parseInt(st.nextToken());
				minutes = Integer.parseInt(st.nextToken());
				return new Time(hours, minutes, 0);
			} catch (NumberFormatException nfe) {
			}
		}
		return null;
	}

	public static Date combineDate(Date date, String time, int defaultHour,
			int defaultMinutes) {
		int hours = defaultHour;
		int minutes = defaultMinutes;
		if (time != null) {
			StringTokenizer st = new StringTokenizer(time, ":");
			if (st.countTokens() == 2) {
				try {
					hours = Integer.parseInt(st.nextToken());
					minutes = Integer.parseInt(st.nextToken());
				} catch (NumberFormatException nfe) {
					hours = defaultHour;
					minutes = defaultMinutes;
				}
			}
		}
		Date dt = new Date(date.getYear(), date.getMonth(), date.getDate(),
				hours, minutes);
		return dt;
	}

	public static Date getDayStart(Date date) {
		Date dt = new Date(date.getYear(), date.getMonth(), date.getDate(), 0,
				0, 0);
		return dt;
	}

	public static int getMonthDays(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(date.getYear(), date.getMonth(), date.getDay());
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static Date getMonthStart(Date date) {
		Date dt = new Date(date.getYear(), date.getMonth(), 1, 0, 0, 0);
		return dt;
	}

	public static Date getMonthEnd(Date date) {
		Date dt = new Date(date.getYear(), date.getMonth(), getMonthDays(date),
				0, 0, 0);
		return dt;
	}

	public static Date addDaysToDate(Date date, long days) {
		return new Date(date.getTime() + (days * 24L * 60L * 60L * 1000L));
	}

	public static Date subtractDaysFromDate(Date date, long days) {
		return new Date(date.getTime() - (days * 24L * 60L * 60L * 1000L));
	}

	private final static SimpleDateFormat SIMPLE_TIME_FORMATER = new SimpleDateFormat(
			"HH:mm");

	private final static SimpleDateFormat SIMPLE_DATE_TIME_FORMATER = new SimpleDateFormat(
			"HH:mm , dd MMM yyyy");

	private final static SimpleDateFormat SIMPLE_DATE_TIME_WITHOUT_YEAR = new SimpleDateFormat(
			"HH:mm , dd MMM");

	private final static SimpleDateFormat SIMPLE_DATE_TIME_12_HOUR_FORMATER = new SimpleDateFormat(
			"hh:mm a, EEE, MMM dd, yyyy");

	private final static SimpleDateFormat SIMPLE_DATE_TIME_FORMATER_FOR_FILENAME = new SimpleDateFormat(
			"HH_m_dd_MMM_yyyy");

	private final static SimpleDateFormat SIMPLE_DATE_FORMATER = new SimpleDateFormat(
			"dd MMM yyyy");

	private final static SimpleDateFormat SIMPLE_DATE_MONTH_FORMATER = new SimpleDateFormat(
			"dd MMM yyyy");

	private final static SimpleDateFormat SIMPLE_MONTH_YEAR_FORMATER = new SimpleDateFormat(
			"MMM yyyy");

	public static String formatTime(Date date) {
		return SIMPLE_TIME_FORMATER.format(date);
	}

	public static String formatDateMonthOnly(Date date) {
		return SIMPLE_DATE_MONTH_FORMATER.format(date);
	}

	public static String formatSize(double dblSize) {
		double ONE_MB = 1024D * 1024D;
		double ONE_KB = 1024D;

		DecimalFormat df = new DecimalFormat("#.##");
		if (dblSize > ONE_MB) {
			return df.format(dblSize / ONE_MB) + " MB";
		} else {
			return df.format(dblSize / ONE_KB) + " KB";
		}
	}

	public static String formatDate(Date date) {
		if (date == null)
			return "None";
		return SIMPLE_DATE_TIME_FORMATER.format(date);
	}

	public static String formatDateWithoutYear(Date date) {
		if (date == null)
			return "None";
		return SIMPLE_DATE_TIME_WITHOUT_YEAR.format(date);
	}

	public static String formatDate12HourFormat(Date date) {
		if (date == null)
			return "None";
		return SIMPLE_DATE_TIME_12_HOUR_FORMATER.format(date);
	}

	public static String formatDateForFileName(Date date) {
		return SIMPLE_DATE_TIME_FORMATER_FOR_FILENAME.format(date);
	}

	public static String formatDateOnly(Date date) {
		return SIMPLE_DATE_FORMATER.format(date);
	}

	public static String formatDateWithMonthYearOnly(Date date) {
		return SIMPLE_MONTH_YEAR_FORMATER.format(date);
	}

	public static String formatDateAsMonthOnly(Date date,
			boolean isFutureDateSpecified) {
		if (date != null) {
			Date now = new Date();
			// If older than 180 days
			long diff = 30L * 6L * 24L * 60L * 60L * 1000L;
			if (isFutureDateSpecified) {
				if ((date.getTime() - now.getTime()) > diff) {
					return OLDER_THAN_180;
				} else {
					return Utils.formatDateWithMonthYearOnly(date);
				}
			} else {
				if ((now.getTime() - date.getTime()) > diff) {
					return OLDER_THAN_180;
				} else {
					return Utils.formatDateWithMonthYearOnly(date);
				}
			}
		}
		return "";
	}

	public static String formatDigits(int num, int digitCount) {
		String numStr = Integer.toString(num);
		if (numStr.length() < digitCount) {
			int remaining = digitCount - numStr.length();
			for (int i = 0; i < remaining; i++)
				numStr = "0" + numStr;
		}
		return numStr;
	}

	public static Date getDayEnd(Date date) {
		Date dt = new Date(date.getYear(), date.getMonth(), date.getDate(), 23,
				59, 59);
		return dt;
	}

	public static Date getNextDay(Date date) {
		Date dt = getDayEnd(date);
		// Add 2 seconds to go to next day
		dt.setTime(dt.getTime() + 2 * 1000);
		return dt;
	}

	public static Date getPreviousDay(Date date) {
		Date dt = getDayStart(date);
		// Minus 2 seconds to go to previous day
		dt.setTime(dt.getTime() - 2 * 1000);
		return dt;
	}

	public static Date getPreviousMonth(Date date) {
		Date dt = getMonthStart(date);
		dt = subtractDaysFromDate(dt, 10);
		return getMonthStart(dt);
	}

	public static Date getNextMonth(Date date) {
		Date dt = getMonthEnd(date);
		dt = addDaysToDate(dt, 10);
		return getMonthStart(dt);
	}

	public static StringBuffer getFileContents(String file) throws Exception {
		InputStream in = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(in);

		char[] chars = new char[5 * 1024];
		int size = 0;
		StringBuffer buffer = new StringBuffer();
		while ((size = reader.read(chars)) != -1) {
			buffer.append(chars, 0, size);
		}
		reader.close();
		in.close();
		return buffer;
	}

	public static StringBuffer getStreamContents(InputStream in)
			throws Exception {
		InputStreamReader reader = new InputStreamReader(in);

		char[] chars = new char[5 * 1024];
		int size = 0;
		StringBuffer buffer = new StringBuffer();
		while ((size = reader.read(chars)) != -1) {
			buffer.append(chars, 0, size);
		}
		reader.close();
		in.close();
		return buffer;
	}

	public static void replaceStringAllOccurances(StringBuffer buffer,
			String searchStr, String replaceStr) {
		int index = -1;
		while ((index = buffer.indexOf(searchStr)) != -1) {
			buffer.replace(index, index + searchStr.length(), replaceStr);
		}
	}

	public static List<Object> getArrayAsList(Object[] array) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}

	public static String getStringArrayAsPipeSeperatedString(String[] array) {
		if (array == null || array.length == 0)
			return null;
		String ret = "";
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null) {
				ret += (i != 0 ? "|" : "") + array[i];
			}
		}
		return ret;
	}

	public static boolean isStringPresentInArray(String str, String[] array) {
		if (str == null || array == null || array.length == 0)
			return false;
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(str))
				return true;
		}
		return false;
	}

	public static boolean isStringPresentInPipeSeperatedString(String toFind,
			String sourceStr) {
		if (toFind == null || sourceStr == null)
			return false;
		StringTokenizer st = new StringTokenizer(sourceStr, "|");
		while (st.hasMoreTokens()) {
			if (st.nextToken().equals(toFind))
				return true;
		}
		return false;
	}

	public static void mergeList(List<String> dest, List<Object> toAdd) {
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		for (Iterator<String> iterator = dest.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			map.put(string, string);
		}
		for (Iterator<Object> iterator = toAdd.iterator(); iterator.hasNext();) {
			String string = iterator.next().toString();
			if (!map.containsKey(string)) {
				dest.add(string);
			}
		}
	}

	public static List<String> getUniqueNewItems(List<String> originalList,
			List<Object> newItems) {
		List<String> ret = new ArrayList<String>();
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		for (Iterator<String> iterator = originalList.iterator(); iterator
				.hasNext();) {
			String string = iterator.next();
			map.put(string, string);
		}
		for (Iterator<Object> iterator = newItems.iterator(); iterator
				.hasNext();) {
			String string = iterator.next().toString();
			if (!map.containsKey(string)) {
				ret.add(string);
			}
		}
		return ret;
	}

	public static File generateTemporaryFile(String prefix, String suffix)
			throws IOException {
		long rand = randomGenerator.nextLong();
		if (rand < 0)
			rand *= -1;
		return File.createTempFile(prefix + rand, suffix);
	}

	public static boolean parseBooleanValue(String str) {
		if (str == null)
			return false;
		if (str.equalsIgnoreCase(YES_STRING))
			return true;
		return false;
	}

	public static String booleanToString(boolean value) {
		return value ? YES_STRING : NO_STRING;
	}

	public static long parseDateString(String str) {
		if (!isNullOrEmpty(str)) {
			StringTokenizer st = new StringTokenizer(str);
			if (st.countTokens() != 2)
				return 0L;
			String value = st.nextToken().trim();
			String unit = st.nextToken().trim();
			long valueLong = 0;
			try {
				valueLong = Long.parseLong(value);
			} catch (NumberFormatException nfe) {
				return 0L;
			}
			if (unit.equalsIgnoreCase("hour") || unit.equalsIgnoreCase("hours")) {
				valueLong = valueLong * (60L * 60L * 1000L);
			} else if (unit.equalsIgnoreCase("day")
					|| unit.equalsIgnoreCase("days")) {
				valueLong = valueLong * (24L * 60L * 60L * 1000L);
			} else if (unit.equalsIgnoreCase("week")
					|| unit.equalsIgnoreCase("weeks")) {
				valueLong = valueLong * (7L * 24L * 60L * 60L * 1000L);
			} else if (unit.equalsIgnoreCase("month")
					|| unit.equalsIgnoreCase("months")) {
				valueLong = valueLong * (30L * 24L * 60L * 60L * 1000L);
			}
			return valueLong;
		}
		return 0L;
	}

	public static String extractMobileNumberAlone(String number) {
		if (number == null)
			return null;
		StringTokenizer st = new StringTokenizer(number, ".");
		if (st.countTokens() < 2)
			return number;
		// Skip STD
		String std = st.nextToken();
		return st.nextToken();
	}

	public static String unescapeHtml(String s) {
		String text = s.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
				.replaceAll("&quot;", "\\\"").replaceAll("&#039;", "\\\\'")
				.replaceAll("&amp;", "&").replaceAll("\n", "")
				.replaceAll("\r", "").replaceAll("\f", "");
		return text;
	}

	public static String encrypt(String s) {
		if (Utils.isNullOrEmpty(s))
			return s;
		try {
			DESKeySpec keySpec = new DESKeySpec(ENCRYPTION_KEY);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(keySpec);

			Cipher c = Cipher.getInstance("DES");
			c.init(Cipher.ENCRYPT_MODE, key);
			String encodeBytes = Base64.getEncoder().encodeToString(s.getBytes());
//			byte[] input = s.getBytes();
//			byte[] cipherText = c.doFinal(input);
//			BASE64Encoder encoder = new BASE64Encoder();
			return encodeBytes;
		} catch (Exception ex) {
			return s;
		}
	}

	public static String decrypt(String s) {
		if (Utils.isNullOrEmpty(s))
			return s;
		try {
			DESKeySpec keySpec = new DESKeySpec(ENCRYPTION_KEY);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(keySpec);

			Cipher c = Cipher.getInstance("DES");
			c.init(Cipher.DECRYPT_MODE, key);

			byte[] plainText = Base64.getDecoder().decode(s);
//			BASE64Decoder decoder = new BASE64Decoder();
//			byte[] input = decoder.decodeBuffer(s);
//			byte[] plainText = c.doFinal(input);
			return new String(plainText);
		} catch (Exception ex) {
			return s;
		}
	}

	public static String encode(String s) {
		if (s == null)
			return null;
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(s);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			if (character == '<') {
				result.append("&lt;");
			} else if (character == '>') {
				result.append("&gt;");
			} else if (character == '\"') {
				result.append("&quot;");
			} else if (character == '\'') {
				result.append("&#039;");
			} else if (character == '&') {
				result.append("&amp;");
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	public static String replaceAll(String str, String searchStr,
			String replaceStr) {
		String regExp = "";
		if (replaceStr == null) {
			replaceStr = "";
		}
		char[] chars = searchStr.toCharArray();
		for (char ch : chars) {
			if (ch == '$') {
				regExp += "\\$";
			} else if (Character.isLetter(ch)) {
				char lower = Character.toLowerCase(ch);
				char upper = Character.toUpperCase(ch);
				regExp += "[" + lower + upper + "]";
			} else {
				regExp += "[" + ch + "]";
			}
		}
		System.out.println("Replace : " + regExp + " in " + str + " with "
				+ replaceStr);
		str = str.replaceAll(regExp, replaceStr);
		System.out.println("After replacement : " + str);
		return str;
	}

	public static String replaceCharacterWithSpacesAround(String str,
			char targetChar, char replaceChar) {
		if (replaceChar == '\0' || targetChar == '\0') {
			return str;
		}
		char[] chars = str.toCharArray();
		StringBuffer retStr = new StringBuffer();
		for (char ch : chars) {
			if (ch == targetChar) {
				retStr.append(" " + replaceChar + " ");
			} else {
				retStr.append(ch);
			}
		}
		System.out.println("After replacement : " + retStr);
		return retStr.toString();
	}

	public static String replaceCharacterWithWord(String str, char targetChar,
			String replaceString) {
		if (replaceString == null || targetChar == '\0') {
			return str;
		}
		char[] chars = str.toCharArray();
		StringBuffer retStr = new StringBuffer();
		for (char ch : chars) {
			if (ch == targetChar) {
				retStr.append(" " + replaceString + " ");
			} else {
				retStr.append(ch);
			}
		}
		System.out.println("After replacement : " + retStr);
		return retStr.toString();
	}

	public String getCurrentTimeInUTC() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utcTime = f.format(new Date());
		return utcTime;
	}

}
