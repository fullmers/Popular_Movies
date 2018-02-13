package com.amiculous.popularmoviesi.data;

/**
 * Created by sarah on 13/02/2018.
 */

public class MovieReview {

    private int id;
    private String author;
    private String content;

    public MovieReview(int id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
    
}
