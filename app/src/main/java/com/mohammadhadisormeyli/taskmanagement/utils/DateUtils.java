package com.mohammadhadisormeyli.taskmanagement.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

    public static final String TIME_FORMAT = "yyyy-MM-dd";
    public static final String ROOM_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final Calendar calendar = Calendar.getInstance();

    @SuppressLint("SimpleDateFormat")
    public static String dateToString(String format, Date date) {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static Date stringToDate(String format, String date) {
        try {
            return new SimpleDateFormat(format, Locale.ENGLISH).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static int getYear(Date date) {
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getDay(Date date) {
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isEqual(Date date1, Date date2) {
        int year1 = getYear(date1);
        int month1 = getMonth(date1);
        int day1 = getDay(date1);

        int year2 = getYear(date2);
        int month2 = getMonth(date2);
        int day2 = getDay(date2);

        return year1 == year2 && month1 == month2 && day1 == day2;
    }

    public static int getDiff(Date date1, Date date2) {
        return (int) ((date1.getTime() - date2.getTime()) / (1000 * 60 * 60 * 24));
    }
}
