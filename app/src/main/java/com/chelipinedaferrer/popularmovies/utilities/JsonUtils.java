package com.chelipinedaferrer.popularmovies.utilities;

import com.chelipinedaferrer.popularmovies.entities.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;

public class JsonUtils {

    public static Movie[] getMoviesFromJson(String jsonData) throws JSONException {
        final String RESULTS = "results";
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

            String originalTitle = movieJson.getString(ORIGINAL_TITLE);
            String overview = movieJson.getString(OVERVIEW);
            double voteAverage = movieJson.getDouble(VOTE_AVERAGE);
            String posterPath = movieJson.getString(POSTER_PATH);
            Date releaseDate = DateUtils.getDateFromString(movieJson.getString(RELEASE_DATE));

            parsedMoviesData[i] = new Movie(originalTitle, overview, voteAverage, posterPath, releaseDate);
        }

        return parsedMoviesData;
    }
}
