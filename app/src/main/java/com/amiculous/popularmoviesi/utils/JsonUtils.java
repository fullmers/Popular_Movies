package com.amiculous.popularmoviesi.utils;

import android.util.Log;

import com.amiculous.popularmoviesi.data.Movie;
import com.amiculous.popularmoviesi.data.MovieVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sarah on 09/02/2018.
 */

public class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    //common:
    private static final String RESULTS = "results";
    private static final String ID = "id";

    //movies:
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String TITLE = "title";
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";

    //videos:
    private static final String KEY = "key";
    private static final String NAME = "name";

    public static ArrayList<Movie> getMoviesFromJson(String jsonString){

        ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONObject baseObject = new JSONObject(jsonString);
            JSONArray resultsArray = baseObject.getJSONArray(RESULTS);

            for (int i = 0; i< resultsArray.length(); i++) {
                JSONObject movie = resultsArray.getJSONObject(i);
                int id = movie.optInt(ID);
                double voteAverage = movie.optDouble(VOTE_AVERAGE);
                String title = movie.optString(TITLE);
                String posterPath = movie.optString(POSTER_PATH);
                String overview = movie.optString(OVERVIEW);
                String releaseDate = movie.optString(RELEASE_DATE);

                Movie thisMovie = new Movie(id, voteAverage, title, posterPath, overview, releaseDate);
                movies.add(thisMovie);
            }

        return movies;
        } catch (JSONException e) {
            Log.d(TAG,e.toString());
        }

        return null;
    }

    public static ArrayList<MovieVideo> getMovieVideosFromJson(String jsonString){
        ArrayList<MovieVideo> videos = new ArrayList<>();
        try {
            JSONObject baseObject = new JSONObject(jsonString);
            int id = baseObject.optInt(ID);
            JSONArray resultsArray = baseObject.getJSONArray(RESULTS);

            for (int i = 0; i< resultsArray.length(); i++) {
                JSONObject video = resultsArray.getJSONObject(i);

                String key = video.optString(KEY);
                URL url = NetworkUtils.buildYoutubeUrl(key);
                String name = video.optString(NAME);

                MovieVideo thisMovieVideo = new MovieVideo(id, url, name);
                videos.add(thisMovieVideo);
            }
            return videos;
        } catch (JSONException e) {
            Log.d(TAG,e.toString());
        }
        return null;
    }
}
