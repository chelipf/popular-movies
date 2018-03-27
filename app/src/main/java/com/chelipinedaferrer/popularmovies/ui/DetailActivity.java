package com.chelipinedaferrer.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.chelipinedaferrer.popularmovies.R;
import com.chelipinedaferrer.popularmovies.entities.Movie;
import com.chelipinedaferrer.popularmovies.lib.ImageLoader;
import com.chelipinedaferrer.popularmovies.lib.PicassoImageLoader;
import com.chelipinedaferrer.popularmovies.utilities.DateUtils;

import static com.chelipinedaferrer.popularmovies.ui.MainActivity.*;

public class DetailActivity extends AppCompatActivity {
    private ImageView moviePosterThumbnail;
    private TextView originalTitle;
    private TextView releaseDate;
    private TextView voteAverage;
    private TextView overview;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        moviePosterThumbnail = findViewById(R.id.movie_poster_thumbnail);
        originalTitle = findViewById(R.id.original_title);
        releaseDate = findViewById(R.id.release_date);
        voteAverage = findViewById(R.id.vote_average);
        overview = findViewById(R.id.overview);

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }
}
