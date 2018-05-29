package com.chelipinedaferrer.popularmovies.utilities;

import android.support.annotation.NonNull;

import com.chelipinedaferrer.popularmovies.entities.Movie;
import com.chelipinedaferrer.popularmovies.entities.Review;
import com.chelipinedaferrer.popularmovies.entities.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class JsonUtils {

    public static Movie[] getMoviesFromJson(@NonNull String jsonData) throws JSONException {
        final String RESULTS = "results";
        final String ID = "id";
        final String ORIGINAL_TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String POSTER_PATH = "poster_path";
        final String RELEASE_DATE = "release_date";

        Movie[] parsedMoviesData = null;

        JSONObject moviesJson = new JSONObject(jsonData);

        JSONArray resultsArrayJson = moviesJson.getJSONArray(RESULTS);

        parsedMoviesData = new Movie[resultsArrayJson.length()];

        for (int i = 0; i < resultsArrayJson.length(); i++) {
            JSONObject movieJson = resultsArrayJson.getJSONObject(i);

            String id = movieJson.getString(ID);
            String originalTitle = movieJson.getString(ORIGINAL_TITLE);
            String overview = movieJson.getString(OVERVIEW);
            double voteAverage = movieJson.getDouble(VOTE_AVERAGE);
            String posterPath = movieJson.getString(POSTER_PATH);
            Date releaseDate = DateUtils.getDateFromString(movieJson.getString(RELEASE_DATE));

            parsedMoviesData[i] = new Movie(id, originalTitle, overview, voteAverage, posterPath, releaseDate);
        }

        return parsedMoviesData;
    }

    public static Trailer[] getTrailersFromJson(@NonNull String jsonData) throws JSONException {
        final String RESULTS = "results";
        final String NAME = "name";
        final String KEY = "key";

        Trailer[] parsedTrailersData = null;

        JSONObject moviesJson = new JSONObject(jsonData);

        JSONArray resultsArrayJson = moviesJson.getJSONArray(RESULTS);

        parsedTrailersData = new Trailer[resultsArrayJson.length()];

        for (int i = 0; i < resultsArrayJson.length(); i++) {
            JSONObject trailerJson = resultsArrayJson.getJSONObject(i);

            String name = trailerJson.getString(NAME);
            String key = trailerJson.getString(KEY);

            parsedTrailersData[i] = new Trailer(name, key);
        }

        return parsedTrailersData;
    }

    public static Review[] getReviewsFromJson(@NonNull String jsonData) throws JSONException {
        final String RESULTS = "results";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String URL = "url";

        Review[] parsedReviewsData = null;

        JSONObject moviesJson = new JSONObject(jsonData);

        JSONArray resultsArrayJson = moviesJson.getJSONArray(RESULTS);

        parsedReviewsData = new Review[resultsArrayJson.length()];

        for (int i = 0; i < resultsArrayJson.length(); i++) {
            JSONObject reviewJson = resultsArrayJson.getJSONObject(i);

            String author = reviewJson.getString(AUTHOR);
            String content = reviewJson.getString(CONTENT);
            String url = reviewJson.getString(URL);

            parsedReviewsData[i] = new Review(author, content, url);
        }

        return parsedReviewsData;
    }
}
