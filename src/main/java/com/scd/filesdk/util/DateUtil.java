package com.scd.filesdk.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author chengdu
 * @date 2019/6/18.
 */
public class DateUtil {

    public static final String YYYYMMDD = "yyyy-MM-dd";

    public static String formatDatetoString(Date date, String pattern){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return  simpleDateFormat.format(date);
    }

    public static void main(String[] args){
        System.out.println(formatDatetoString(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }
}
