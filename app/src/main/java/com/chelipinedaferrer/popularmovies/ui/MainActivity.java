package com.chelipinedaferrer.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie[]>,
        SharedPreferences.OnSharedPreferenceChangeListener,
        MovieAdapter.MovieAdapterOnClickHandler {
    public static final String EXTRA_MOVIE = "com.chelipinedaferrer.popularmovies.extra_movie";
    private static final int MOVIES_LOADER_ID = 11;

    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    private MovieAdapter movieAdapter;

    private RecyclerView recyclerView;
    private TextView errorMessage;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        errorMessage = (TextView) findViewById(R.id.loading_error_message);
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);


        int numOfColumns = 4;
        GridLayoutManager layoutManager = new GridLayoutManager(this, numOfColumns);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.image_padding);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        movieAdapter = new MovieAdapter(this);
        recyclerView.setAdapter(movieAdapter);

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
                    URL moviesURL = NetworkUtils.buildUrl(MainActivity.this, moviesOrder);
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
        errorMessage.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
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

    /**
     * Decorate the Items of th GridLayout with the same offset space.
     */
    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
        private int itemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            this.itemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(itemOffset, itemOffset, itemOffset, itemOffset);
        }
    }
}
