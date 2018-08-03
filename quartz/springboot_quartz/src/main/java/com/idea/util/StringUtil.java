package com.idea.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;


public class StringUtil {

	private static Logger logger = Logger.getLogger(StringUtil.class);
	private static Pattern pattern = Pattern.compile("[0-9]*");

	public static String nullToSpace(String str) {
		if (str == null) {
			return "";
		}
		return str;
	}

	public static String spaceToBlank(String str) {
		if ("".equals(nullToSpace(str))) {
			return " ";
		}
		return str;
	}

	public static String listToString(String[] strList, String sep) {
		if (strList == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strList.length; i++) {
			if (i == 0) {
				sb.append(strList[i]);
			} else {
				sb.append(sep + strList[i]);
			}
		}
		return sb.toString();
	}

	public static String listToString(List<String> strList, String sep) {
		if (strList == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strList.size(); i++) {
			if (i == 0) {
				sb.append((String) strList.get(i));
			} else {
				sb.append(sep + (String) strList.get(i));
			}
		}
		return sb.toString();
	}

	public static String listToStringAdd(String[] strList, String sep) {
		if (strList == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strList.length; i++) {
			if (i == 0) {
				sb.append("'" + strList[i] + "'");
			} else {
				sb.append(sep + "'" + strList[i] + "'");
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	public static String listToStringAdd(List<?> list, String sep, String name) {
		if (list == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			String s = map.get(name) == null ? "" : map.get(name).toString();
			String[] ss = s.split(sep);
			for (int j = 0; j < ss.length; j++) {
				if ("".equals(sb.toString())) {
					sb.append("'" + ss[j] + "'");
				} else {
					sb.append(sep + "'" + ss[j] + "'");
				}
			}
		}
		if ("".equals(sb.toString())) {
			sb.append("''");
		}
		return sb.toString();
	}

	public static Date stringToDate(String str, String format) {
		if ((str != null) && (!"".equals(str))) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date date = null;
			try {
				date = sdf.parse(str);
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
			}
			return date;
		}
		return null;
	}

	public static String dateToString(Date date, String format) {
		String s = "";
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			s = sdf.format(date);
		}
		return s;
	}

	public static int getBetweenDays(Date startDate, Date endDate) {
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		startCal.setTime(startDate);
		endCal.setTime(endDate);
		int betweenYears = endCal.get(1) - startCal.get(1);
		int betweenDays = endCal.get(6) - startCal.get(6);
		for (int i = 0; i < betweenYears; i++) {
			startCal.set(1, startCal.get(1) + 1);
			betweenDays += startCal.getActualMaximum(6);
		}
		return betweenDays;
	}

	public static Date getNextDate(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(5);
		cal.set(5, day + n);
		return cal.getTime();
	}

	public static Date getBeforeDate(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(5);
		cal.set(5, day - n);
		return cal.getTime();
	}

	public static List<Date> getDateList(String start, String end) {
		List<Date> returnList = new ArrayList<Date>();
		Date startDate = stringToDate(start, "yyyy-MM-dd");
		Date endDate = stringToDate(end, "yyyy-MM-dd");
		if (startDate != null) {
			for (Date s = startDate; s.before(endDate); s = getNextDate(s, 1)) {
				returnList.add(s);
			}
		}

		return returnList;
	}

	public static Date getEndOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(5, 1);
		cal.roll(5, -1);
		return cal.getTime();
	}

	public static boolean isRemind(Date date, int i) {
		boolean b = false;
		date = getBeforeDate(date, i);
		if (date.compareTo(new Date()) >= 0) {
			b = true;
		}
		return b;
	}

	public static boolean isWeekEnd(Date date) {
		boolean b = false;
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		int dayOfWeek = ca.get(7);
		if ((dayOfWeek == 1) || (dayOfWeek == 7)) {
			b = true;
		}
		return b;
	}

	public static String createNumberString(int length) {
		String numberChar = "0123456789";
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
		}
		return sb.toString();
	}

	public static boolean ipIsValid(String ipSection, String ip) {
		if (ipSection == null) {
			throw new NullPointerException("IP段不能为空！");
		}
		if (ip == null) {
			throw new NullPointerException("IP不能为空！");
		}
		ipSection = ipSection.trim();
		ip = ip.trim();
		if ((!ipSection
				.matches("((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\-((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)"))
				|| (!ip.matches("((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)"))) {
			return false;
		}
		int idx = ipSection.indexOf('-');
		String[] sips = ipSection.substring(0, idx).split("\\.");
		String[] sipe = ipSection.substring(idx + 1).split("\\.");
		String[] sipt = ip.split("\\.");
		long ips = 0L;
		long ipe = 0L;
		long ipt = 0L;
		for (int i = 0; i < 4; i++) {
			ips = ips << 8 | Integer.parseInt(sips[i]);
			ipe = ipe << 8 | Integer.parseInt(sipe[i]);
			ipt = ipt << 8 | Integer.parseInt(sipt[i]);
		}
		if (ips > ipe) {
			long t = ips;
			ips = ipe;
			ipe = t;
		}
		return (ips <= ipt) && (ipt <= ipe);
	}

	public static String changeNumToDate(String s) {
		String rtn = "1900-01-01";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date1 = new Date();
			date1 = format.parse("1900-01-01");
			long i1 = date1.getTime();
			i1 = i1 / 1000L + (Long.parseLong(s) - 2L) * 24L * 3600L;
			date1.setTime(i1 * 1000L);
			rtn = format.format(date1);
		} catch (Exception e) {
			rtn = "1900-01-01";
		}
		return rtn;
	}

	public static String toUNICODE(String s) {
		if (s == null){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) <= 'Ā') {
				sb.append("\\u00");
			} else {
				sb.append("\\u");
			}
			sb.append(Integer.toHexString(s.charAt(i)));
		}
		return sb.toString();
	}

	/*
	 * public static String UnicodeToString(String str) { Pattern pattern =
	 * Pattern.compile("(\\\\u(\\p{XDigit}{4}))"); Matcher matcher =
	 * pattern.matcher(str); while (matcher.find()) { char ch =
	 * (char)Integer.parseInt(matcher.group(2), 16); str =
	 * str.replace(matcher.group(1), ch); } return str; }
	 */
	public static String getDateTime(String datetime) {
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(Long.parseLong(datetime));
		return dateToString(ca.getTime(), "yyyy-MM-dd");
	}

	public static boolean isNumeric(String str) {
		Matcher isNum = pattern.matcher(str);
		return (isNum.matches()) && (!"".equals(str));
	}

	public static boolean containsLowerCase(String str) {
		if (str == null || str.length() == 0) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isLowerCase(str.charAt(i)) == true) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取不同字符长度
	 */
	public static int length(String s) {
		if (s == null) {
			return 0;
		}
		char[] c = s.toCharArray();
		int len = 0;
		for (int i = 0; i < c.length; i++) {
			len++;
			if (!isLetter(c[i])) {
				len++;
			}
		}
		return len;
	}

	public static boolean isLetter(char c) {
		int k = 0x80;
		return c / k == 0 ? true : false;
	}

	public static boolean isSpecialLetter(String s) {
		String regEx = "[\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

		Pattern p = Pattern.compile(regEx);

		Matcher m = p.matcher(s);
		boolean bool = m.find();
		return bool;
	}

	// 根据Unicode编码完美的判断中文汉字和符号
	private static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}

	// 完整的判断中文汉字和符号
	public static boolean containChinese(String strName) {
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isIpAddr(String text) {
		if (text != null && !text.isEmpty()) {
			// 定义正则表达式
			String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
			// 判断ip地址是否与正则表达式匹配
			if (text.matches(regex)) {
				// 返回判断信息
				return true;
			} else {
				// 返回判断信息
				return false;
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		System.out.println(isSpecialLetter("*asdf"));
	}
}


