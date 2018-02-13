package com.amiculous.popularmoviesi.data;

import java.net.URL;

/**
 * Created by sarah on 13/02/2018.
 */

public class MovieVideo {

    private int id;
    private URL youtubeURL;
    private String name;

    public MovieVideo(int id, URL youtubeURL, String name) {
        this.id = id;
        this.youtubeURL = youtubeURL;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public URL getYoutubeURL() {
        return youtubeURL;
    }

    public String getName() {
        return name;
    }
}
