package com.chelipinedaferrer.popularmovies.entities;

public class Review {
    private String author;
    private String content;
    private String url;

    public Review(String author, String content, String url) {
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
