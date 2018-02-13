package com.amiculous.popularmoviesi;

import java.util.ArrayList;

/**
 * Created by sarah on 13/02/2018.
 */

public class MovieExtras {

    private int movieId;
    private ArrayList<String> youtubeVideoKeys;
    private ArrayList<String> reviews;

    public MovieExtras(int movieId) {
        this.movieId = movieId;
        this.youtubeVideoKeys = new ArrayList<String>();
        this.reviews = new ArrayList<String>();
    }

    public MovieExtras(int movieId, ArrayList<String> youtubeVideoKeys, ArrayList<String> reviews) {
        this.movieId = movieId;
        this.youtubeVideoKeys = youtubeVideoKeys;
        this.reviews = reviews;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public ArrayList<String> getYoutubeVideoKeys() {
        return youtubeVideoKeys;
    }

    public void setYoutubeVideoKeys(ArrayList<String> youtubeVideoKeys) {
        this.youtubeVideoKeys = youtubeVideoKeys;
    }

    public ArrayList<String> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<String> reviews) {
        this.reviews = reviews;
    }
}