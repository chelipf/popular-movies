package com.chelipinedaferrer.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.net.Uri.Builder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.chelipinedaferrer.popularmovies.BuildConfig;
import com.chelipinedaferrer.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class NetworkUtils {
    private static final String POPULAR_MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String API_KEY_PARAM = "api_key";

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String API_KEY = BuildConfig.POPULAR_MOVIES_API_KEY;


    /**
     * Builds the Popular Movies URL used to talk to the popular movies server using a sort rule.
     *
     * @param context The context.
     * @param order   The sort order rule.
     * @return The URL to use to query the popular movies server.
     */
    public static URL buildMoviesUrl(Context context, String order) {
        final String POPULAR_MOVIES_PATH = context.getString(R.string.pref_sort_order_popular_value);
        final String TOP_RATED_MOVIES_PATH = context.getString(R.string.pref_sort_order_top_rate_value);

        Builder uriBuilder = Uri.parse(POPULAR_MOVIES_BASE_URL).buildUpon();

        if (order.equals(POPULAR_MOVIES_PATH)) {
            uriBuilder.appendPath(POPULAR_MOVIES_PATH);
        } else if (order.equals(TOP_RATED_MOVIES_PATH)) {
            uriBuilder.appendPath(TOP_RATED_MOVIES_PATH);
        } else {
            return null;
        }

        uriBuilder.appendQueryParameter(API_KEY_PARAM, API_KEY);

        Uri popularMoviesUri = uriBuilder.build();

        return buildUrl(popularMoviesUri);
    }

    /**
     * Builds the Trailers URL used to talk to the popular movies server using a movie id.
     *
     * @param id The movie id.
     * @return The URL to use to query the popular movies server.
     */
    public static URL buildTrailersUrl(@NonNull String id) {
        final String TRAILERS_PATH = "videos";

        Builder uriBuilder = Uri.parse(POPULAR_MOVIES_BASE_URL).buildUpon();

        uriBuilder.appendPath(id);
        uriBuilder.appendPath(TRAILERS_PATH);
        uriBuilder.appendQueryParameter(API_KEY_PARAM, API_KEY);

        Uri trailersUri = uriBuilder.build();

        return buildUrl(trailersUri);
    }

    /**
     * Builds the Reviews URL used to talk to the popular movies server using a movie id.
     *
     * @param id The movie id.
     * @return The URL to use to query the popular movies server.
     */
    public static URL buildReviewsUrl(@NonNull String id) {
        final String REVIEWS_PATH = "reviews";

        Builder uriBuilder = Uri.parse(POPULAR_MOVIES_BASE_URL).buildUpon();

        uriBuilder.appendPath(id);
        uriBuilder.appendPath(REVIEWS_PATH);
        uriBuilder.appendQueryParameter(API_KEY_PARAM, API_KEY);

        Uri trailersUri = uriBuilder.build();

        return buildUrl(trailersUri);
    }

    /**
     * Builds the URL using a Uri.
     *
     * @param uri
     * @return The URL to use to query the popular movies server.
     */
    public static URL buildUrl(Uri uri) {
        try {
            URL url = new URL(uri.toString());
            Log.v(TAG, "URL: " + url);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}
