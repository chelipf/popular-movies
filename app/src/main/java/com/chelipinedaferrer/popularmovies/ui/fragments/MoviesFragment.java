package com.chelipinedaferrer.popularmovies.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chelipinedaferrer.popularmovies.R;
import com.chelipinedaferrer.popularmovies.common.SettingsManager;
import com.chelipinedaferrer.popularmovies.entities.Movie;
import com.chelipinedaferrer.popularmovies.ui.DetailActivity;
import com.chelipinedaferrer.popularmovies.ui.GridAutofitLayoutManager;
import com.chelipinedaferrer.popularmovies.ui.adapters.MovieAdapter;
import com.chelipinedaferrer.popularmovies.utilities.JsonUtils;
import com.chelipinedaferrer.popularmovies.utilities.NetworkUtils;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Movie[]>,
        SharedPreferences.OnSharedPreferenceChangeListener,
        MovieAdapter.MovieAdapterOnClickHandler {
    @BindView(R.id.recyclerview_movies)
    RecyclerView recyclerviewMovies;
    @BindView(R.id.loading_error_message)
    TextView loadingErrorMessage;
    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;

    public static final String EXTRA_MOVIE = "com.chelipinedaferrer.popularmovies.extra_movie";
    private static final int MOVIES_LOADER_ID = 11;
    private static final String RECYCLERVIEW_DATA = "recyclerview_data";
    private static final String RECYCLERVIEW_STATE = "recyclerview_position";

    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    private MovieAdapter movieAdapter;
    private Context context;
    private Movie[] mMovies;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this, rootView);

        int numOfColumns = 4;
        GridLayoutManager layoutManager = new GridAutofitLayoutManager(context, 250);

        recyclerviewMovies.setLayoutManager(layoutManager);
        recyclerviewMovies.setHasFixedSize(true);

        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        movieAdapter = new MovieAdapter(this);
        recyclerviewMovies.setAdapter(movieAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(RECYCLERVIEW_DATA)) {
            Movie[] data = (Movie[]) savedInstanceState.getParcelableArray(RECYCLERVIEW_DATA);
            movieAdapter.setMoviesData(data);
            mMovies = data;
        } else {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(MOVIES_LOADER_ID, null, this);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(RECYCLERVIEW_STATE)) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(RECYCLERVIEW_STATE);
            recyclerviewMovies.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArray(RECYCLERVIEW_DATA, mMovies);
        outState.putParcelable(RECYCLERVIEW_STATE, recyclerviewMovies.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onStart() {
        super.onStart();

        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            getLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public Loader<Movie[]> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<Movie[]>(context) {
            private Movie[] movies;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (movies != null) {
                    deliverResult(movies);
                } else {
                    loadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public Movie[] loadInBackground() {
                String moviesOrder = SettingsManager.getSortOrder(context);
                if (moviesOrder == null || TextUtils.isEmpty(moviesOrder)) {
                    return null;
                }

                try {
                    URL moviesURL = NetworkUtils.buildMoviesUrl(context, moviesOrder);
                    String moviesJson = NetworkUtils.getResponseFromHttpUrl(moviesURL);
                    movies = JsonUtils.getMoviesFromJson(moviesJson);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                return movies;
            }

            @Override
            public void deliverResult(Movie[] data) {
                super.deliverResult(data);

                movies = data;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Movie[]> loader, Movie[] data) {
        loadingIndicator.setVisibility(View.INVISIBLE);
        movieAdapter.setMoviesData(data);
        mMovies = data;

        if (null == data) {
            showErrorMessage();
        } else {
            showMoviesDataView();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Movie[]> loader) {
    }

    private void showMoviesDataView() {
        loadingErrorMessage.setVisibility(View.INVISIBLE);
        recyclerviewMovies.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerviewMovies.setVisibility(View.INVISIBLE);
        loadingErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }

    @Override
    public void onClick(Movie movie) {
        Intent detailIntent = new Intent(context, DetailActivity.class);
        detailIntent.putExtra(EXTRA_MOVIE, movie);
        startActivity(detailIntent);
    }
}
