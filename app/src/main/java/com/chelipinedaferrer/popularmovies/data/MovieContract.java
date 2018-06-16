package com.chelipinedaferrer.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {
    public static final String AUTHORITY = "com.chelipinedaferrer.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_ID = "id";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final Uri CONTENT_URI_ID = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).appendPath(PATH_ID).build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";

        public static Uri buildMovieUriWithApiId(String id) {
            return CONTENT_URI_ID.buildUpon().appendPath(id).build();
        }
    }
}
