package com.moneymaker.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created for MoneyMaker by Jay Damon on 8/27/2016.
 */
public class FormatDate {

    public static final String CALENDAR_DISPLAY_DATE = "dd-MMM-yy";
    public static final String SQL_INPUT_DATE = "yyyy-MM-dd";

    public String formatDate(String date)  {
        Date initDate = null;
        try {
            initDate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        return formatter.format(initDate);
    }

    public String formatFromTableDate(String date) {
        Date initDate = null;
        try {
            initDate = new SimpleDateFormat("dd-MMM-yy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        return formatter.format(initDate);
    }

    public Calendar parseStringCalendar(String date) {

        Calendar calendarDate = Calendar.getInstance();
        Date dateForCalendar = stringToDate(date);
        calendarDate.setTime(dateForCalendar);

        return calendarDate;
    }

    private Date stringToDate(String date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date convertDate = new Date();
        try {
            convertDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertDate;
    }
}
