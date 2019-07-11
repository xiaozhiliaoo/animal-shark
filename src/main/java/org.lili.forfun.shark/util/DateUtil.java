package org.lili.forfun.shark.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;


public final class DateUtil {
    private DateUtil() {
    }

    public static String getNow() {
        LocalDateTime now = LocalDateTime.now();
        return now.getYear() + "_" + now.getMonthValue() + "_" + now.getDayOfMonth() + "_" + now.getHour() + ":" + now.getMinute();
    }

    public static String makeResultDir() {
        LocalDateTime now = LocalDateTime.now();
        return now.getYear() + "_" + now.getMonthValue() + "_" + now.getDayOfMonth() + "_" + now.getHour() + ":" + now.getMinute() + ":" + now.getSecond();
    }

    /**
     * 时间戳转当前日期字符串
     *
     * @param s
     * @return
     */
    private static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String stampToDate(Long s) {
        return stampToDate(Long.toString(s));
    }

}
