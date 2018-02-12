package com.amiculous.popularmoviesi;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.amiculous.popularmoviesi.utils.JsonUtils;
import com.amiculous.popularmoviesi.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sarah on 09/02/2018.
 */

public class MovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private static final String TAG = MovieLoader.class.getSimpleName();

    private ArrayList<Movie> mMovies;
    private URL mUrl;


    public MovieLoader(Context context) {
        super(context);
        mUrl = NetworkUtils.buildUrl(context);
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        try {
            String response = NetworkUtils.getResponseFromHttpUrl(mUrl);
            return JsonUtils.getMoviesFromJson(response);
        } catch (IOException e) {
            Log.d(TAG,e.toString());
        }
        return null;
    }

    @Override
    public void deliverResult(ArrayList<Movie> data) {
        super.deliverResult(data);
        mMovies = data;
    }

//    https://stackoverflow.com/questions/7474756/onloadfinished-not-called-after-coming-back-from-a-home-button-press
// This function override was needed to solve bug that movie list was not being refreshed after
    //changing sort setting and hitting back button from SettingsFragment
    //it was not enough to simply implement the OnSharedPreferencesChangedListener in MainActivity
    @Override
    protected void onStartLoading() {
        if (mMovies != null) {
            deliverResult(mMovies);
        }

        if (takeContentChanged() || mMovies == null) {
            forceLoad();
        }
    }
}
