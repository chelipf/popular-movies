package com.chelipinedaferrer.popularmovies.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chelipinedaferrer.popularmovies.R;
import com.chelipinedaferrer.popularmovies.entities.Movie;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private Movie[] moviesData;
    private final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342";

    /**
     * Creates a MovieAdapter.
     *
     */
    public MovieAdapter() {
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie movie = moviesData[position];

        final int radius = 10;
        final int margin = 10;
        final Transformation transformation = new RoundedCornersTransformation(radius, margin);
        Picasso.get()
                .load(MOVIE_POSTER_BASE_URL + movie.getPosterPath())
                .transform(transformation)
                .into(holder.moviePosterImage);
    }

    @Override
    public int getItemCount() {
        if (null == moviesData) return 0;
        return moviesData.length;
    }

    /**
     * This method is used to set the movies on a MoviesAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MovieAdapter to display it.
     *
     * @param moviesData The new weather data to be displayed.
     */
    public void setMoviesData(Movie[] moviesData) {
        this.moviesData = moviesData;
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a movie list item.
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder {

        public final ImageView moviePosterImage;

        public MovieAdapterViewHolder(View view) {
            super(view);

            moviePosterImage = (ImageView) view.findViewById(R.id.movie_poster_image);
        }
    }
}
