package com.amiculous.popularmoviesi.data;

import java.util.ArrayList;

/**
 * Created by sarah on 13/02/2018.
 */

public class MovieExtras {

    private int movieId;
    private ArrayList<MovieVideo> youtubeVideos;
    private ArrayList<String> reviews;

    public MovieExtras(int movieId) {
        this.movieId = movieId;
        this.youtubeVideos = new ArrayList<MovieVideo>();
        this.reviews = new ArrayList<String>();
    }

    public MovieExtras(int movieId, ArrayList<MovieVideo> youtubeVideos, ArrayList<String> reviews) {
        this.movieId = movieId;
        this.youtubeVideos = youtubeVideos;
        this.reviews = reviews;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public ArrayList<MovieVideo> getYoutubeVideos() {
        return youtubeVideos;
    }

    public void setYoutubeVideos(ArrayList<MovieVideo> youtubeVideos) {
        this.youtubeVideos = youtubeVideos;
    }

    public ArrayList<String> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<String> reviews) {
        this.reviews = reviews;
    }
}