package com.chelipinedaferrer.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieContentProvider extends ContentProvider {
    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;
    public static final int MOVIE_WITH_API_ID = 102;

    private static final UriMatcher matcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/" + MovieContract.PATH_ID + "/#", MOVIE_WITH_API_ID);

        return uriMatcher;
    }

    private MovieDbHelper movieDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        movieDbHelper = new MovieDbHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = movieDbHelper.getReadableDatabase();

        int match = matcher.match(uri);

        Cursor cursor;
        String id;
        String mSelection;
        String[] mSelectionArgs;

        switch (match) {
            case MOVIES:
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_WITH_ID:
                id = uri.getPathSegments().get(1);

                mSelection = "_id = ?";
                mSelectionArgs = new String[]{id};
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;
            case MOVIE_WITH_API_ID:
                id = uri.getPathSegments().get(2);

                mSelection = "id = ?";
                mSelectionArgs = new String[]{id};
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        int match = matcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknow Uri:" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        int match = matcher.match(uri);
        int moviesDeleted = 0;
        String id;
        String mWhere;
        String[] mWhereArgs;

        switch (match) {
            case MOVIE_WITH_ID:
                id = uri.getPathSegments().get(1);

                mWhere = "_id = ?";
                mWhereArgs = new String[]{id};
                moviesDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, mWhere, mWhereArgs);
                break;

            case MOVIE_WITH_API_ID:
                id = uri.getPathSegments().get(2);

                mWhere = "id = ?";
                mWhereArgs = new String[]{id};
                moviesDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, mWhere, mWhereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (moviesDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}