package com.chelipinedaferrer.popularmovies.lib;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class PicassoImageLoader implements ImageLoader {
    private final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342";
    private Picasso picassoInstance;

    public PicassoImageLoader() {
        picassoInstance = Picasso.get();
    }

    @Override
    public void loadRoundedCornersPoster(ImageView image, String path) {
        final int radius = 10;
        final int margin = 10;
        final Transformation transformation = new RoundedCornersTransformation(radius, margin);

        picassoInstance
                .load(MOVIE_POSTER_BASE_URL + path)
                .transform(transformation)
                .into(image);
    }
}
