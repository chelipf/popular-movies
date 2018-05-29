package com.chelipinedaferrer.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chelipinedaferrer.popularmovies.R;
import com.chelipinedaferrer.popularmovies.common.SettingsManager;
import com.chelipinedaferrer.popularmovies.entities.Movie;
import com.chelipinedaferrer.popularmovies.utilities.JsonUtils;
import com.chelipinedaferrer.popularmovies.utilities.NetworkUtils;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie[]>,
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

    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        int numOfColumns = 4;
        GridLayoutManager layoutManager = new GridAutofitLayoutManager(this, 250);

        recyclerviewMovies.setLayoutManager(layoutManager);
        recyclerviewMovies.setHasFixedSize(true);

        movieAdapter = new MovieAdapter(this);
        recyclerviewMovies.setAdapter(movieAdapter);

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(MOVIES_LOADER_ID, null, this);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public Loader<Movie[]> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<Movie[]>(this) {
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
                String moviesOrder = SettingsManager.getSortOrder(MainActivity.this);
                if (moviesOrder == null || TextUtils.isEmpty(moviesOrder)) {
                    return null;
                }

                try {
                    URL moviesURL = NetworkUtils.buildMoviesUrl(MainActivity.this, moviesOrder);
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
    public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {
        loadingIndicator.setVisibility(View.INVISIBLE);
        movieAdapter.setMoviesData(data);

        if (null == data) {
            showErrorMessage();
        } else {
            showMoviesDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }

    @Override
    public void onClick(Movie movie) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra(EXTRA_MOVIE, movie);
        startActivity(detailIntent);
    }

    public static class GridAutofitLayoutManager extends GridLayoutManager {
        private int mColumnWidth;
        private boolean mColumnWidthChanged = true;

        public GridAutofitLayoutManager(Context context, int columnWidth) {
            super(context, 1);
            setColumnWidth(checkedColumnWidth(context, columnWidth));
        }

        public GridAutofitLayoutManager(Context context, int columnWidth, int orientation, boolean reverseLayout) { /* Initially set spanCount to 1, will be changed automatically later. */
            super(context, 1, orientation, reverseLayout);
            setColumnWidth(checkedColumnWidth(context, columnWidth));
        }

        private int checkedColumnWidth(Context context, int columnWidth) {
            if (columnWidth <= 0) { /* Set default columnWidth value (48dp here). It is better to move this constant to static constant on top, but we need context to convert it to dp, so can't really do so. */
                columnWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, context.getResources().getDisplayMetrics());
            }

            return columnWidth;
        }

        public void setColumnWidth(int newColumnWidth) {
            if (newColumnWidth > 0 && newColumnWidth != mColumnWidth) {
                mColumnWidth = newColumnWidth;
                mColumnWidthChanged = true;
            }
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            if (mColumnWidthChanged && mColumnWidth > 0) {
                int totalSpace;
                if (getOrientation() == VERTICAL) {
                    totalSpace = getWidth() - getPaddingRight() - getPaddingLeft();
                } else {
                    totalSpace = getHeight() - getPaddingTop() - getPaddingBottom();
                }

                int spanCount = Math.max(1, totalSpace / mColumnWidth);
                setSpanCount(spanCount);
                mColumnWidthChanged = false;
            }

            super.onLayoutChildren(recycler, state);
        }
    }
}
