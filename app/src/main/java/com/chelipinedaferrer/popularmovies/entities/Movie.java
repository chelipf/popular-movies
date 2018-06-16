package com.chelipinedaferrer.popularmovies.entities;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.chelipinedaferrer.popularmovies.data.MovieContract;

import java.util.Date;

public class Movie implements Parcelable {
    private String id;
    private String originalTitle;
    private String overview;
    private double voteAverage;
    private String posterPath;
    private Date releaseDate;

    public Movie(String id, String originalTitle, String overview, double voteAverage, String posterPath, Date releaseDate) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
    }

    private Movie(Parcel in) {
        this.id = in.readString();
        this.originalTitle = in.readString();
        this.overview = in.readString();
        this.voteAverage = in.readDouble();
        this.posterPath = in.readString();
        this.releaseDate = new Date(in.readLong());
    }

    public String getId() {
        return id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public boolean isFavorite(Context context) {
        boolean isFavorite = false;

        if (TextUtils.isEmpty(id)) {
            return false;
        }

        Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.buildMovieUriWithApiId(id), null, null, null, null);

        if (cursor != null) {
            isFavorite = cursor.moveToFirst();
            cursor.close();
        }
        return isFavorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(originalTitle);
        parcel.writeString(overview);
        parcel.writeDouble(voteAverage);
        parcel.writeString(posterPath);
        parcel.writeLong(releaseDate.getTime());
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
