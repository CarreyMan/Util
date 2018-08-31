package util;

import com.rerloan.basic.exception.TranFailException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {

    /**
     * 要用到的DATE Format的定义
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE = "1970-01-01 08:00:01";
    public static final SimpleDateFormat SHORTDATEFORMAT = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    public static long transToSecond(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return transToSecond(sdf.parse(date));
        } catch (Exception e) {}
        return 0;
    }

    public static long transToSecond(Date date) {
        return date.getTime() / 1000;
    }

    public static String format(long second, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(second * 1000));
    }

    public static Date midnightOfToday() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        //时分秒（毫秒数）
        long millisecond = hour*60*60*1000 + minute*60*1000 + second*1000;
        //凌晨00:00:00
        calendar.setTimeInMillis(calendar.getTimeInMillis()-millisecond);
        return calendar.getTime();
    }

    public static Timestamp strToTimestamp(String dateStr) {
        return Timestamp.valueOf(dateStr);
    }

    /*
	 * 6、字符串转换成日期
	 */
    public static Date StringToDate(String date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 7、日期转换成字符串
     *
     * @param date
     *            日期对象
     * @param pattern
     *            转换成字符串格式 例如："yyyyMMddHHmmss"
     * @return
     * @throws TranFailException
     */
    public static String DateToString(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static String TimeStampToString(Timestamp timestamp) {
        return TimeStampToString(timestamp, DATETIME_FORMAT);
    }
    public static String TimeStampToString(Timestamp timestamp, String pattern) {
        if(timestamp==null) return "";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(timestamp);
    }

    /**
     * date  string to string
     */
    public static String StringToString(String date, String pattern1 , String pattern2)
            throws TranFailException {
        Date dates = StringToDate(date,pattern1);
        String signTimes = DateTimeUtil.DateToString(dates ,pattern2);
        return signTimes;
    }

    public static Timestamp getNowTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * yyyy-MM-dd 当前日期
     *
     */
    public static String getReqDate() {
        return SHORTDATEFORMAT.format(new Date());
    }

    /**
     * yyyy-MM-dd 传入日期
     *
     * @param date
     * @return
     */
    public static String getReqDate(Date date) {
        return SHORT_DATE_FORMAT.format(date);
    }

    /**
     * 计算 day 天后的时间
     *
     * @param date
     * @param day
     * @return
     */
    public static Date addDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }
    /**
     * 计算两个Calendar相差天数(忽略时分秒毫秒)
     */
    public static int between_days(Calendar calendar1,Calendar calendar2){
        LocalDate localDate1=LocalDate.of(calendar1.get(Calendar.YEAR),calendar1.get(Calendar.MONTH)+1,calendar1.get(Calendar.DATE));
        LocalDate localDate2=LocalDate.of(calendar2.get(Calendar.YEAR),calendar2.get(Calendar.MONTH)+1,calendar2.get(Calendar.DATE));
        return Integer.valueOf(String.valueOf(localDate1.until(localDate2, ChronoUnit.DAYS)));
    }

    /**
     * 获取localdate的毫秒数(忽略时分秒)
     * @param localDate
     * @return
     */
    public static long getLongOfLocalDate(LocalDate localDate){
        return localDate.atTime(0,0,0,0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
