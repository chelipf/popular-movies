package com.chelipinedaferrer.popularmovies.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chelipinedaferrer.popularmovies.R;
import com.chelipinedaferrer.popularmovies.data.MovieContract;
import com.chelipinedaferrer.popularmovies.entities.Movie;
import com.chelipinedaferrer.popularmovies.entities.Review;
import com.chelipinedaferrer.popularmovies.entities.Trailer;
import com.chelipinedaferrer.popularmovies.lib.ImageLoader;
import com.chelipinedaferrer.popularmovies.lib.PicassoImageLoader;
import com.chelipinedaferrer.popularmovies.ui.adapters.ReviewAdapter;
import com.chelipinedaferrer.popularmovies.ui.adapters.TrailerAdapter;
import com.chelipinedaferrer.popularmovies.utilities.DateUtils;
import com.chelipinedaferrer.popularmovies.utilities.JsonUtils;
import com.chelipinedaferrer.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.chelipinedaferrer.popularmovies.ui.fragments.MoviesFragment.EXTRA_MOVIE;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler, ReviewAdapter.ReviewAdapterOnClickHandler {
    @BindView(R.id.detail_scrollview)
    ScrollView scrollView;
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
    @BindView(R.id.recyclerview_reviews)
    RecyclerView recyclerviewReviews;
    @BindView(R.id.loading_error_message_reviews)
    TextView loadingErrorMessageReviews;
    @BindView(R.id.loading_indicator_reviews)
    ProgressBar loadingIndicatorReviews;
    @BindView(R.id.add_favorite_fab)
    FloatingActionButton favoriteFab;

    private static final int TRAILERS_LOADER_ID = 12;
    private static final int REVIEWS_LOADER_ID = 13;
    private static final String IS_FAVORITE = "is_favorite";
    private static final String SCROLLVIEW_POSITION = "scrollview_position";

    private Movie movie;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private boolean isFavorite;

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

        if (savedInstanceState != null && savedInstanceState.containsKey(IS_FAVORITE)) {
            isFavorite = savedInstanceState.getBoolean(IS_FAVORITE);
        } else if (movie != null) {
            isFavorite = movie.isFavorite(this);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(SCROLLVIEW_POSITION)) {
            scrollView.setScrollY(savedInstanceState.getInt(SCROLLVIEW_POSITION));
        }

        setfavoriteFabColor();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        LinearLayoutManager layoutManagerTrailers = new LinearLayoutManager(this);
        recyclerviewTrailers.setLayoutManager(layoutManagerTrailers);
        recyclerviewTrailers.setHasFixedSize(true);

        trailerAdapter = new TrailerAdapter(this);
        recyclerviewTrailers.setAdapter(trailerAdapter);

        LoaderManager loaderManager = getSupportLoaderManager();

        // Trailers
        LoaderManager.LoaderCallbacks<Trailer[]> trailersTask = new LoaderManager.LoaderCallbacks<Trailer[]>() {
            @NonNull
            @Override
            public Loader<Trailer[]> onCreateLoader(int id, @Nullable Bundle args) {
                return new AsyncTaskLoader<Trailer[]>(DetailActivity.this) {
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

            @Override
            public void onLoaderReset(@NonNull Loader<Trailer[]> loader) {

            }
        };

        loaderManager.initLoader(TRAILERS_LOADER_ID, null, trailersTask);

        // Reviews
        LinearLayoutManager layoutManagerReviews = new LinearLayoutManager(this);
        recyclerviewReviews.setLayoutManager(layoutManagerReviews);
        recyclerviewReviews.setHasFixedSize(true);

        reviewAdapter = new ReviewAdapter(this);
        recyclerviewReviews.setAdapter(reviewAdapter);

        LoaderManager.LoaderCallbacks<Review[]> reviewsTask = new LoaderManager.LoaderCallbacks<Review[]>() {
            @NonNull
            @Override
            public Loader<Review[]> onCreateLoader(int id, @Nullable Bundle args) {
                return new AsyncTaskLoader<Review[]>(DetailActivity.this) {
                    private Review[] reviews;

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();

                        if (reviews != null) {
                            deliverResult(reviews);
                        } else {
                            loadingIndicatorReviews.setVisibility(View.VISIBLE);
                            forceLoad();
                        }
                    }

                    @Override
                    public Review[] loadInBackground() {
                        String id = movie.getId();
                        if (id == null || TextUtils.isEmpty(id)) {
                            return null;
                        }

                        try {
                            URL reviewsURL = NetworkUtils.buildReviewsUrl(id);
                            String reviewsJson = NetworkUtils.getResponseFromHttpUrl(reviewsURL);
                            reviews = JsonUtils.getReviewsFromJson(reviewsJson);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }

                        return reviews;
                    }

                    @Override
                    public void deliverResult(Review[] data) {
                        super.deliverResult(data);

                        reviews = data;
                    }
                };
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Review[]> loader, Review[] data) {
                loadingIndicatorReviews.setVisibility(View.INVISIBLE);
                reviewAdapter.setReviewsData(data);

                if (null == data) {
                    showTrailersErrorMessage();
                } else {
                    showTrailersDataView();
                }
            }

            @Override
            public void onLoaderReset(@NonNull Loader<Review[]> loader) {

            }
        };

        loaderManager.initLoader(REVIEWS_LOADER_ID, null, reviewsTask);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
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
    public void onClick(Trailer movie) {
        String videoId = movie.getKey();

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));

        // Check if the youtube app exists on the device
        if (intent.resolveActivity(getPackageManager()) == null) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId));
        }

        startActivity(intent);
    }

    @Override
    public void onClick(Review review) {
        Uri reviewUri = Uri.parse(review.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, reviewUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void addFavorite(View view) {
        if (isFavorite) {
            int moviesDeleted = deleteFavoriteMovie();

            if (moviesDeleted != 0) {
                isFavorite = false;
                setfavoriteFabColor();
                Toast.makeText(getBaseContext(), getString(R.string.favorite_movie_removed), Toast.LENGTH_LONG).show();
            }
        } else {
            Uri uri = insertFavoriteMovie();

            if (uri != null) {
                isFavorite = true;
                setfavoriteFabColor();
                Toast.makeText(getBaseContext(), getString(R.string.favorite_movie_added), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(IS_FAVORITE, isFavorite);

        int scrollviewPosition = scrollView.getScrollY();
        outState.putInt(SCROLLVIEW_POSITION, scrollviewPosition);
    }

    private Uri insertFavoriteMovie() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_ID, movie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        String releaseDate = formatter.format(movie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);

        return getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
    }

    private int deleteFavoriteMovie() {
        return getContentResolver().delete(MovieContract.MovieEntry.buildMovieUriWithApiId(movie.getId()), null, null);
    }

    private void setfavoriteFabColor() {
        if (isFavorite) {
            favoriteFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.favorite)));
        } else {
            favoriteFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.notFavorite)));
        }
    }
}
