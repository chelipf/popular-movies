package com.chelipinedaferrer.popularmovies.utilities;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
            return formatter.format(date);
        } else {
            return null;
        }
    }
}
