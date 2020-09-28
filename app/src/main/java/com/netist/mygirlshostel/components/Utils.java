package com.netist.mygirlshostel.components;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 12/19/2017.
 */

public class Utils {

    public static String currentServerDateTime()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(Calendar.getInstance().getTime());
    }

    public static String formatDate(String date, String time) throws ParseException {

        Date initDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date + " " + time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String parsedDate = formatter.format(initDate);

        return parsedDate;
    }

    public static String formatDate (String dateTime) throws ParseException {

        Date initDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String parsedDate = formatter.format(initDate);

        return parsedDate;
    }

    public static String formatTime (String dateTime) throws ParseException {

        Date initDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String parsedTime = formatter.format(initDate);

        return parsedTime;
    }

    public static String formatDate2 (String date) throws ParseException {

        Date initDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String parsedDate = formatter.format(initDate);

        return parsedDate;
    }

    public static boolean isPrevDate(String base, String test) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date baseDate = formatter.parse(base);
        Date testDate = formatter.parse(test);

        long diff = baseDate.getTime() - testDate.getTime();
        if (diff < 0)
            return false;

        return true;
    }

    public static Float getMonths(String startDate, String endDate) throws ParseException {
        if (startDate.equals("") || endDate.equals(""))
            return 0.0f;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = formatter.parse(startDate);
        Date end = formatter.parse(endDate);

        long diff = end.getTime() - start.getTime();

        float months = diff / (1000 * 60 * 60 * 24);
        String m = String.format("%.02f", (float)(months / 30));

        return Float.parseFloat(m);
    }
}
