package com.amiculous.popularmoviesi;

import android.content.Context;
import android.support.annotation.Nullable;
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
    private Context mContext;

    public MovieLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Nullable
    @Override
    public ArrayList<Movie> loadInBackground() {
        URL url = NetworkUtils.buildUrl(mContext);
        try {
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            return JsonUtils.getMoviesFromJson(response);
        } catch (IOException e) {
            Log.d(TAG,e.toString());
        }
        return null;
    }
}
