package com.chelipinedaferrer.popularmovies.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chelipinedaferrer.popularmovies.R;
import com.chelipinedaferrer.popularmovies.data.MovieContract;
import com.chelipinedaferrer.popularmovies.entities.Movie;
import com.chelipinedaferrer.popularmovies.ui.DetailActivity;
import com.chelipinedaferrer.popularmovies.ui.GridAutofitLayoutManager;
import com.chelipinedaferrer.popularmovies.ui.adapters.MovieAdapter;
import com.chelipinedaferrer.popularmovies.utilities.DateUtils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Movie[]>,
        MovieAdapter.MovieAdapterOnClickHandler {
    @BindView(R.id.recyclerview_movies)
    RecyclerView recyclerviewMovies;
    @BindView(R.id.loading_error_message)
    TextView loadingErrorMessage;
    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;

    public static final String EXTRA_MOVIE = "com.chelipinedaferrer.popularmovies.extra_movie";
    public static final int FAVORITES_LOADER_ID = 12;

    private MovieAdapter movieAdapter;
    private Context context;

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

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        movieAdapter = new MovieAdapter(this);
        recyclerviewMovies.setAdapter(movieAdapter);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(FAVORITES_LOADER_ID, null, this);
    }

    @Override
    public Loader<Movie[]> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<Movie[]>(context) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                loadingIndicator.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public Movie[] loadInBackground() {
                Movie[] movies;
                try {
                    Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                    if (cursor != null) {
                        movies = new Movie[cursor.getCount()];
                    } else {
                        return null;
                    }

                    int i = 0;

                    while (cursor.moveToNext()) {
                        int idIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID);
                        int originaltitleIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
                        int overviewIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
                        int voteAverageIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
                        int posterPathIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
                        int releaseDateIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);


                        String id = cursor.getString(idIndex);
                        String originalTitle = cursor.getString(originaltitleIndex);
                        String overview = cursor.getString(overviewIndex);
                        double voteAverage = cursor.getDouble(voteAverageIndex);
                        String posterPath = cursor.getString(posterPathIndex);
                        Date releaseDate = DateUtils.getDateFromString(cursor.getString(releaseDateIndex));

                        movies[i] = new Movie(id, originalTitle, overview, voteAverage, posterPath, releaseDate);
                        i++;
                    }

                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                return movies;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Movie[]> loader, Movie[] data) {
        loadingIndicator.setVisibility(View.INVISIBLE);
        movieAdapter.setMoviesData(data);

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
    public void onClick(Movie movie) {
        Intent detailIntent = new Intent(context, DetailActivity.class);
        detailIntent.putExtra(EXTRA_MOVIE, movie);
        startActivity(detailIntent);
    }
}
