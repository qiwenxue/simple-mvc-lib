package core.util;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 日期转换工具类
 * 
 * @author qiwx
 *
 */
@SuppressWarnings("unused")
public class DateUtil {

  public static String PATTERN = "yyyy-MM-dd";

  public static String PATTERN_MONTH = "yyyy-MM";

  public static String PATTEN_SECOND = "yyyy-MM-dd HH:mm:ss";

  public static String PATTERN_WEEK = "yyyy-MM-dd EEE";

  public static long DAY = 24 * 60 * 60 * 1000;

  public static long HOUR = 60 * 60 * 1000;

  public static long MINIT = 60 * 1000;

  private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static SimpleDateFormat dateFormatS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
  private static SimpleDateFormat dateFormatWeek = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");// 带有星期几的日期格式
  public static String DATE_FORMAT_NOSPLIT = "yyyyMMddHHmmssSSS";
  private static SimpleDateFormat dateFormatEn = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
  private static SimpleDateFormat dateFormatCn = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINESE);
  private static SimpleDateFormat dateFormatYMEn = new SimpleDateFormat("MMM-yyyy", Locale.ENGLISH);
  private static SimpleDateFormat dateFormatYMCn = new SimpleDateFormat("yyyy年MM月", Locale.CHINESE);
  private static SimpleDateFormat dateFormatMDEn = new SimpleDateFormat("dd-MMM", Locale.ENGLISH);
  private static SimpleDateFormat dateFormatMDCn = new SimpleDateFormat("MM-dd", Locale.CHINESE);

  public static java.util.Date getCurrentTime() {

    return new Timestamp(System.currentTimeMillis());
  }

  /**
   * 把日期类型转换为日期字符串
   * 
   * @param date
   * @param pattern
   * @return
   * @throws ParseException
   */
  public static String parseDate(Date d, String pattern) {
    SimpleDateFormat df = null;
    if (pattern == null || pattern.length() < 1) {
      df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
    } else {
      df = new SimpleDateFormat(pattern);
    }
    return df.format(d);
  }

  /**
   * 把日期类型转换为日期字符串
   * 
   * @param date
   * @param pattern
   * @return
   * @throws ParseException
   */
  public static String parseDate(Date d) {
    SimpleDateFormat df = null;
    df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
    return df.format(d);
  }

  /**
   * 把日期字符串转换为日期类型
   * 
   * @param date
   * @param pattern
   * @return
   * @throws ParseException
   * @throws java.text.ParseException
   */
  public static Date parseDate(String date, String pattern) throws Exception {
    SimpleDateFormat df = null;
    if (pattern == null || pattern.length() < 1) {
      try {
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      df = new SimpleDateFormat(pattern, Locale.CHINESE);
    }
    if (date != null) {
      return df.parse(date);
    }
    return null;
  }

  /**
   * 判断2个日期的大小,第2个参数减去第1个参数
   * 
   * @param start
   * @param end
   * @return
   * @throws ParseException
   * @throws java.text.ParseException
   */
  public static boolean isBig(String start, String end, String param) throws Exception {
    if (start == null || start.length() == 0) {
      start = "0";
    }
    if (end == null || end.length() == 0) {
      end = "0";
    }

    Date first = parseDate(start, param);
    Date second = parseDate(end, param);
    if (second.getTime() - first.getTime() > 0) {
      return true;
    }
    return false;
  }

  /**
   * 某个日期的下一天
   * 
   * @param date
   * @param pattern
   * @param cnt
   * @return
   * @throws Exception
   */
  public static Date afterDate(String date, String pattern, int cnt) throws Exception {
    Date start = parseDate(date, pattern);
    Calendar cal = Calendar.getInstance();
    cal.setTime(start);
    cal.add(Calendar.DATE, cnt);
    // cal.set(Calendar.DATE, cal.get(Calendar.DATE)+cnt);
    return cal.getTime();
  }

  /**
   * 某个日期的前cnt天
   * 
   * @param date
   * @param pattern
   * @param cnt
   * @return
   * @throws Exception
   */
  public static Date beforeDate(String date, String pattern, int cnt) throws Exception {
    return afterDate(date, pattern, 0 - cnt);
  }

  /**
   * 某个日期的下一天
   * 
   * @param date
   * @param pattern
   * @param cnt
   * @return
   * @throws Exception
   */
  public static String afterDateStr(String date, String pattern, int cnt) throws Exception {
    Date start = afterDate(date, pattern, cnt);
    String val = parseDate(start, pattern);
    return val;
  }

  /**
   * 某个日期的前cnt天
   * 
   * @param date
   * @param pattern
   * @param cnt
   * @return
   * @throws Exception
   */
  public static String beforDateStr(String date, String pattern, int cnt) throws Exception {
    Date start = beforeDate(date, pattern, cnt);
    String val = parseDate(start, pattern);
    return val;
  }

  /**
   * 两个日期的天数差
   * 
   * @param begin
   * @param end
   * @param pattern
   * @return
   * @throws Exception
   */
  public static double getDateDiff(String begin, String end, String pattern) throws Exception {
    Date beginDate = parseDate(begin, pattern);
    Date endDate = parseDate(end, pattern);
    double diff = toDiff(beginDate, endDate, DAY);
    return round(diff, null);
  }

  /**
   * 两个时间的毫秒差
   * 
   * @param date1
   * @param date2
   * @return
   * @throws Exception
   */
  public static long getDateDiffMinSec(String date1, String date2) throws Exception {
    Date beginDate = parseDateByFormat(date1);
    Date endDate = parseDateByFormat(date2);
    Long d1 = beginDate.getTime();
    Long d2 = endDate.getTime();
    return d2 - d1;
  }

  /**
   * 计算两天的差
   * 
   * @param first
   * @param second
   * @param dFlag
   *          天 小时 分钟
   * @return
   */
  public static double toDiff(Date first, Date second, long dFlag) {
    if (first != null && second != null) {
      return (second.getTime() - first.getTime()) * 100 / (dFlag * 100.0);
    } else if (first != null && second == null) {
      return 0;
    } else if (first == null && second != null) {
      return 0;
    } else if (first == null && second == null) {
      return -1;
    }
    return -1;
  }

  /**
   * 相隔多少小时
   * 
   * @param begin
   * @param end
   * @param pattern
   * @return
   * @throws Exception
   */
  public static double getHourDiff(String begin, String end, String pattern) throws Exception {
    Date beginDate = parseDate(begin, pattern);
    Date endDate = parseDate(end, pattern);
    double diff = toDiff(beginDate, endDate, HOUR);
    return round(diff, null);
  }

  /**
   * 相隔多少分钟
   * 
   * @param begin
   * @param end
   * @param pattern
   * @return
   * @throws Exception
   */
  public static double getMinitDiff(String begin, String end, String pattern) throws Exception {
    Date beginDate = parseDate(begin, pattern);
    Date endDate = parseDate(end, pattern);
    double diff = toDiff(beginDate, endDate, MINIT);
    return round(diff, null);
  }

  /**
   * 计算精度
   */
  public static double round(double number, String partten) {
    if (partten == null || partten == "") {
      partten = "#.00";
    }
    return Double.parseDouble(new DecimalFormat(partten).format(number));
  }

  /**
   * 把字符串转化为Date
   * 
   * @param dateStr
   * @return
   * @throws java.text.ParseException
   */
  public static Date parseDateByFormat(String dateStr) throws ParseException, java.text.ParseException {
    if (dateStr == null || "".equals(dateStr)) {
      return null;
    }

    SimpleDateFormat format = null;
    if (Pattern.matches("\\d{4}-\\d{1,2}-\\d{1,2}", dateStr)) {
      format = new SimpleDateFormat("yyyy-MM-dd");
    } else if (Pattern.matches("\\d{4}\\d{2}\\d{2}", dateStr)) {
      format = new SimpleDateFormat("yyyyMMdd");
    } else if (Pattern.matches("\\d{4}年\\d{2}月\\d{2}日", dateStr)) {
      format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
    } else if (Pattern.matches("\\d{4}年\\d{1,2}月\\d{1,2}日", dateStr)) {
      format = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
    } else if (Pattern.matches("\\d{1,2}\\w{3}\\d{4}", dateStr)) {
      format = new SimpleDateFormat("dMMMyyyy", Locale.ENGLISH);
    } else if (Pattern.matches("\\d{1,2}-\\w{3}-\\d{4}", dateStr)) {
      format = new SimpleDateFormat("d-MMM-yyyy", Locale.ENGLISH);
    } else if (dateStr.length() > 20) {
      format = dateFormatS;
    } else {
      format = dateFormat;
    }
    return format.parse(dateStr);
  }

  public static String timestamp2DateStr(Long stimestamp) {
    return dateFormat.format(stimestamp * 1000);
  }

  /**
   * 取得日期的下一个月
   * 
   * @param now
   * @param pattern
   * @param cnt
   * @return
   * @throws Exception
   */
  public static Date afterMonth(String now, String pattern, int cnt) throws Exception {
    Date start = parseDate(now, pattern);
    Calendar cal = Calendar.getInstance();
    cal.setTime(start); // 当前的时间
    cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + cnt);
    return cal.getTime();
  }

  /**
   * 取得日期的下一个年
   * 
   * @param now
   * @param pattern
   * @param cnt
   * @return
   * @throws Exception
   */
  public static Date afterYear(String now, String pattern, int cnt) throws Exception {
    Date start = parseDate(now, pattern);
    Calendar cal = Calendar.getInstance();
    cal.setTime(start); // 当前的时间
    cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + cnt);
    return cal.getTime();
  }

  public static List<Date> getBeforeDateList(String now, String PATTERN2, int cnt) throws Exception {
    List<Date> dl = new ArrayList<Date>();
    for (int i = 0; i < cnt; i++) {
      Date d = beforeDate(now, PATTERN2, i);
      dl.add(d);
    }
    Collections.sort(dl);
    return dl;
  }

  public static List<String> getBeforeDateList(Date now, String PATTERN2, int cnt) throws Exception {
    List<String> dl = new ArrayList<String>();
    for (int i = 0; i < cnt; i++) {
      Date d = beforeDate(parseDate(now, PATTERN2), PATTERN2, i);
      String time = parseDate(d, PATTERN2);
      if (dl.contains(time)) {
        continue;
      }
      dl.add(time);
    }
    Collections.sort(dl);
    return dl;
  }

  /**
   * 两个日期之间有多少天
   * 
   * @param now
   * @param last
   * @param pattern
   * @return
   * @throws Exception
   */
  public static List<String> getDateList(Date now, Date last, String pattern) throws Exception {
    String begin = parseDate(now, pattern);
    String end = parseDate(last, pattern);
    int diff = (int) (getDateDiff(begin, end, pattern));
    return getBeforeDateList(now, pattern, Math.abs(diff));
  }

  /**
   * 返回从now开始的以前的cnt个月
   * 
   * @param now
   * @param cnt
   * @return
   * @throws Exception
   */
  public static List<Date> getMonthList(String now, String PATTERN2, int cnt) throws Exception {
    List<Date> dl = new ArrayList<Date>();
    for (int i = 0; i < cnt; i++) {
      Date d = afterMonth(now, PATTERN2, (0 - i));
      dl.add(d);
    }
    Collections.sort(dl);
    return dl;
  }

  public static List<Date> getYearList(String now, String PATTERN2, int cnt) throws Exception {
    List<Date> dl = new ArrayList<Date>();
    for (int i = 0; i < cnt; i++) {
      Date d = afterYear(now, PATTERN2, (0 - i));
      dl.add(d);
    }
    Collections.sort(dl);
    return dl;
  }

  /**
   * 获得这个日期是年中第几周
   * 
   * @param date
   * @return
   * @throws Exception
   */
  public static String getWeekInYear(String date) throws Exception {
    Calendar cal = Calendar.getInstance();
    Date start = parseDate(date, "yyyy-MM-dd");
    cal.setTime(start);
    int k = cal.get(Calendar.WEEK_OF_YEAR);
    return k + "";
  }

  /**
   * 获得这个日期是年中第几周
   * 
   * @param date
   * @return
   * @throws Exception
   */
  public static String getWeekInYear(Date date) throws Exception {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int k = cal.get(Calendar.WEEK_OF_YEAR);
    return k + "";
  }

  /**
   * 获得这个日期是月中的第几周
   * 
   * @param date
   * @return
   * @throws Exception
   */
  public static String getWeekInMonth(String date) throws Exception {
    Calendar cal = Calendar.getInstance();
    Date start = parseDate(date, "yyyy-MM-dd");
    cal.setTime(start);
    int k = cal.get(Calendar.WEEK_OF_MONTH);
    return k + "";
  }

  /**
   * 获得这个日期是月中的第几周
   * 
   * @param date
   * @return
   * @throws Exception
   */
  public static String getWeekInMonth(Date date) throws Exception {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int k = cal.get(Calendar.WEEK_OF_MONTH);
    return k + "";
  }

  /**
   * 输入一个日期，获得本星期的日期如:(20110301-----20110307) date 格式 2011-02-12
   * 
   * @return
   * @throws Exception
   */
  public static List<String> getSelfWeek(String date, String patten) throws Exception {
    Calendar cal = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
    Date ymdDate = parseDate(date, patten);
    cal.setTime(ymdDate);
    int today = cal.get(Calendar.DAY_OF_WEEK);
    List<String> list = new ArrayList<String>();
    for (int i = 0; i < today - 1; i++) {
      list.add(DateUtil.beforDateStr(date, patten, i));
    }
    for (int j = today; j < 8; j++) {
      list.add(DateUtil.afterDateStr(date, patten, j - today + 1));
    }
    Collections.sort(list);
    return list;
  }

  /**
   * 得到本月的第一天
   */
  public static String getMonthFirstDay(boolean withTime) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
    String result = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    if (withTime) {
      result += " 00:00:00";
    }
    return result;
  }

  /**
   * 得到本月的最后一天
   */
  public static String getMonthLastDay(boolean withTime) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // 设置日期,最后一天
    String result = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    if (withTime) {
      result += " 23:59:59";
    }
    return result;
  }

  /**
   * 获得本年某个月的开始一天
   * 
   * @param month
   * @return
   */
  public static String getMonthFirstDayOfThisYear(int month, boolean withTime) {
    Calendar calendar = Calendar.getInstance();
    int mth = month - 1;
    calendar.set(Calendar.MONTH, mth); // 设置月份
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));// 设置日期,第一天
    String result = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    if (withTime) {
      result += " 00:00:00";
    }
    return result;
  }

  /**
   * 获得本年某个月的最后一天
   * 
   * @param month
   * @return
   */
  public static String getMonthLastDayOfThisYear(int month, boolean withTime) {
    Calendar calendar = Calendar.getInstance();
    int mth = month - 1;
    calendar.set(Calendar.MONTH, mth); // 设置月份
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));// 设置日期,最后一天
    String result = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    if (withTime) {
      result += " 00:00:00";
    }
    return result;
  }

  public static Date formatDate(Date date, String pattern) {
    Date now = null;
    try {
      now = DateUtil.parseDate(DateUtil.parseDate(new Date(), pattern), pattern);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return now;
  }

  public static void main(String[] args) throws Exception {
    Date d = DateUtil.parseDate("20170103121212", "yyyyMMddHHmmss");
    System.out.println(d);
  }
}
