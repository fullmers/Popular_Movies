package com.amiculous.popularmoviesi.loaders;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.amiculous.popularmoviesi.MovieDetailActivity;
import com.amiculous.popularmoviesi.MovieExtras;
import com.amiculous.popularmoviesi.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by sarah on 13/02/2018.
 */

public class MovieExtrasLoader extends AsyncTaskLoader<MovieExtras> {
    private final String TAG = ApiMovieLoader.class.getSimpleName();

    private MovieExtras mMovieExtras;
    private int mMovieId;

    public MovieExtrasLoader(Context context, int movieId) {
        super(context);
        mMovieId = movieId;
        mMovieExtras = new MovieExtras(movieId);
    }

    @Nullable
    @Override
    public MovieExtras loadInBackground() {
        URL reviewsURL = NetworkUtils.buildExtrasUrl(mMovieId, MovieDetailActivity.MovieExtraTypes.REVIEWS);
        URL videosURL = NetworkUtils.buildExtrasUrl(mMovieId, MovieDetailActivity.MovieExtraTypes.VIDEOS);

        Log.d(TAG,reviewsURL.toString());
        Log.d(TAG,videosURL.toString());

        try {
            String reviewsResponse = NetworkUtils.getResponseFromHttpUrl(reviewsURL);
            Log.d(TAG,"Reviews: \n" + reviewsResponse);
            String videosResponse = NetworkUtils.getResponseFromHttpUrl(videosURL);
            Log.d(TAG,"Videos: \n" + videosResponse);
            return mMovieExtras;
        } catch (IOException e) {
            Log.d(TAG,e.toString());
        }
        return null;
    }
}
