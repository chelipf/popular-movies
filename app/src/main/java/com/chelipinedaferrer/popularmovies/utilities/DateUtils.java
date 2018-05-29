package com.chelipinedaferrer.popularmovies.utilities;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static Date getDateFromString(String dateString) {
        Date date;

        SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = databaseFormat.parse(dateString);
        } catch (ParseException e) {
            date = null;
        }

        return date;
    }

    public static String formatLocaleDate(@NonNull Date date) {
        if (date != null) {
            DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
            return formatter.format(date);
        } else {
            return null;
        }
    }
}
