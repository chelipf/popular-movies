package com.chelipinedaferrer.popularmovies.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.chelipinedaferrer.popularmovies.R;

public class SettingsManager {

    /**
     * Returns the sort order currently set in Preferences. The default sort order this method
     * will return is "popular".
     *
     * @param context Context used to get the SharedPreferences
     * @return Sort Order The current user has set in SharedPreferences. Will default to
     * popular if SharedPreferences have not been implemented yet.
     */
    public static String getSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForSortOrder = context.getString(R.string.pref_sort_order_key);
        String defaultSortOrder = context.getString(R.string.pref_sort_order_popular_value);
        return prefs.getString(keyForSortOrder, defaultSortOrder);
    }
}
