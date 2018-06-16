package com.chelipinedaferrer.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chelipinedaferrer.popularmovies.data.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "popularmovies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_ID + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " DATE NOT NULL DEFAULT CURRENT_DATE, " +
                "CONSTRAINT api_id_unique UNIQUE(ID));";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
