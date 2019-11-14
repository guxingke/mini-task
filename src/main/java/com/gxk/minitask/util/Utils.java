package com.gxk.minitask.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class Utils {

  public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  public static final SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy-MM-dd");

  public static String format(Date date) {
    return SDF.format(date);
  }

  public static Date parse(String raw) {
    try {
      return SDF.parse(raw);
    } catch (ParseException e) {
      return null;
    }
  }

  public static String currentWeekFormat() {
    Date date = getWeekStartDate();
    return SDF2.format(date);
  }

  public static Date getWeekStartDate() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    Date date = cal.getTime();
    return date;
  }

  public static int whichDayInCurrentWeek() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    return cal.get(Calendar.DAY_OF_WEEK) + 1;
  }

  public static Date thisFriday() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
    cal.set(Calendar.HOUR_OF_DAY, 19);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    Date date = cal.getTime();
    return date;
  }

  public static Date thisDay() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 19);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    Date date = cal.getTime();
    return date;
  }
}
