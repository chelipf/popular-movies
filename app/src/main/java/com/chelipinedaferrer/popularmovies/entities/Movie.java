package com.chelipinedaferrer.popularmovies.entities;

import java.util.Date;

public class Movie {
    private String originalTitle;
    private String overview;
    private double voteAverage;
    private String posterPath;
    private Date releaseDate;

    public Movie(String originalTitle, String overview, double voteAverage, String posterPath, Date releaseDate) {
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
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
}
