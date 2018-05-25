package com.chelipinedaferrer.popularmovies.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chelipinedaferrer.popularmovies.R;
import com.chelipinedaferrer.popularmovies.entities.Movie;
import com.chelipinedaferrer.popularmovies.entities.Trailer;
import com.chelipinedaferrer.popularmovies.lib.ImageLoader;
import com.chelipinedaferrer.popularmovies.lib.PicassoImageLoader;
import com.chelipinedaferrer.popularmovies.utilities.DateUtils;
import com.chelipinedaferrer.popularmovies.utilities.JsonUtils;
import com.chelipinedaferrer.popularmovies.utilities.NetworkUtils;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.chelipinedaferrer.popularmovies.ui.MainActivity.EXTRA_MOVIE;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Trailer[]>, TrailerAdapter.TrailerAdapterOnClickHandler {
    @BindView(R.id.movie_poster_thumbnail)
    ImageView moviePosterThumbnail;
    @BindView(R.id.original_title)
    TextView originalTitle;
    @BindView(R.id.release_date)
    TextView releaseDate;
    @BindView(R.id.vote_average)
    TextView voteAverage;
    @BindView(R.id.overview)
    TextView overview;
    @BindView(R.id.recyclerview_trailers)
    RecyclerView recyclerviewTrailers;
    @BindView(R.id.loading_error_message_trailers)
    TextView loadingErrorMessageTrailers;
    @BindView(R.id.loading_indicator_trailers)
    ProgressBar loadingIndicatorTrailers;

    private static final int TRAILERS_LOADER_ID = 12;

    private Movie movie;
    private TrailerAdapter trailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(EXTRA_MOVIE)) {
                movie = intentThatStartedThisActivity.getParcelableExtra(EXTRA_MOVIE);

                ImageLoader imageLoader = new PicassoImageLoader();
                imageLoader.loadRoundedCornersPoster(moviePosterThumbnail, movie.getPosterPath());
                originalTitle.setText(movie.getOriginalTitle());
                releaseDate.setText(DateUtils.formatLocaleDate(movie.getReleaseDate()));
                voteAverage.setText(String.valueOf(movie.getVoteAverage()));
                overview.setText(movie.getOverview());
            }
        }

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerviewTrailers.setLayoutManager(layoutManager);
        recyclerviewTrailers.setHasFixedSize(true);

        trailerAdapter = new TrailerAdapter(this);
        recyclerviewTrailers.setAdapter(trailerAdapter);

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(TRAILERS_LOADER_ID, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Trailer[]> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Trailer[]>(this) {
            private Trailer[] trailers;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (trailers != null) {
                    deliverResult(trailers);
                } else {
                    loadingIndicatorTrailers.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public Trailer[] loadInBackground() {
                String id = movie.getId();
                if (id == null || TextUtils.isEmpty(id)) {
                    return null;
                }

                try {
                    URL trailersURL = NetworkUtils.buildTrailersUrl(id);
                    String trailersJson = NetworkUtils.getResponseFromHttpUrl(trailersURL);
                    trailers = JsonUtils.getTrailersFromJson(trailersJson);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                return trailers;
            }

            @Override
            public void deliverResult(Trailer[] data) {
                super.deliverResult(data);

                trailers = data;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Trailer[]> loader, Trailer[] data) {
        loadingIndicatorTrailers.setVisibility(View.INVISIBLE);
        trailerAdapter.setTrailersData(data);

        if (null == data) {
            showTrailersErrorMessage();
        } else {
            showTrailersDataView();
        }
    }

    private void showTrailersDataView() {
        loadingErrorMessageTrailers.setVisibility(View.INVISIBLE);
        recyclerviewTrailers.setVisibility(View.VISIBLE);
    }

    private void showTrailersErrorMessage() {
        recyclerviewTrailers.setVisibility(View.INVISIBLE);
        loadingErrorMessageTrailers.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Trailer[]> loader) {

    }

    @Override
    public void onClick(Trailer movie) {
        String videoId = movie.getKey();

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));

        // Check if the youtube app exists on the device
        if (intent.resolveActivity(getPackageManager()) == null) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId));
        }

        startActivity(intent);
    }
}
