package com.amiculous.popularmoviesi.utils;

import android.util.Log;

import com.amiculous.popularmoviesi.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sarah on 09/02/2018.
 */

public class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    private static final String RESULTS = "results";
    private static final String VOTE_COUNT = "vote_count";
    private static final String ID = "id";
    private static final String VIDEO = "video";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String TITLE = "title";
    private static final String POPULARITY = "popularity";
    private static final String POSTER_PATH = "poster_path";
    private static final String ORIGINAL_LANGUAGE = "original_language";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String GENRE_IDS = "genre_ids";
    private static final String BACKDDROP_PATH = "backdrop_path";
    private static final String ADULT = "adult";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";

    public static ArrayList<Movie> getMoviesFromJson(String jsonString){

        ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONObject baseObject = new JSONObject(jsonString);
            JSONArray resultsArray = baseObject.getJSONArray(RESULTS);

            for (int i = 0; i< resultsArray.length(); i++) {
                JSONObject movie = resultsArray.getJSONObject(i);
                int voteCount = movie.optInt(VOTE_COUNT);
                int id = movie.optInt(ID);
                boolean isVideo = movie.optBoolean(VIDEO);
                double voteAverage = movie.optDouble(VOTE_AVERAGE);
                String title = movie.optString(TITLE);
                double popularity = movie.optDouble(POPULARITY);
                String posterPath = movie.optString(POSTER_PATH);
                String originalLanguage = movie.optString(ORIGINAL_LANGUAGE);
                String originalTitle = movie.optString(ORIGINAL_TITLE);
                JSONArray genreIdsJSON = movie.getJSONArray(GENRE_IDS);
                String backdropPath = movie.optString(BACKDDROP_PATH);
                boolean isAdult = movie.optBoolean(ADULT);
                String overview = movie.optString(OVERVIEW);
                String releaseDate = movie.optString(RELEASE_DATE);

                int numGenres = genreIdsJSON.length();
                int[] genreIds = new int[numGenres];
                for (int j = 0; j < numGenres; j++) {
                    genreIds[j] = genreIdsJSON.getInt(j);
                }

                Movie thisMovie = new Movie(voteCount, id, isVideo, voteAverage, title, popularity, posterPath,
                originalLanguage, originalTitle, genreIds, backdropPath, isAdult, overview, releaseDate);
                movies.add(thisMovie);
            }

        return movies;
        } catch (JSONException e) {
            Log.d(TAG,e.toString());
        }

        return null;
    }
}
