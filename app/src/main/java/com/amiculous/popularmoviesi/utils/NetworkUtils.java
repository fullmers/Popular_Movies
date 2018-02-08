package com.amiculous.popularmoviesi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.amiculous.popularmoviesi.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by sarah on 08/02/2018.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String SORT_BY_POPULARITY = "https://api.themoviedb.org/3/movie/popular/";
    private static final String SORT_BY_TOP_RATED = "https://api.themoviedb.org/3/movie/top_rated/";

    public static URL buildUrl(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForSortOrder = context.getString(R.string.pref_sort_by_key);
        String defaultSortOrder = context.getString(R.string.pref_sort_by_popularity);
        String sortOrder = sp.getString(keyForSortOrder, defaultSortOrder);

        Uri movieQueryUri;
        String apiKeyLabel = context.getString(R.string.api_key_label);
        String apiKeyValue = context.getString(R.string.api_key);
        if (sortOrder.equals(context.getString(R.string.pref_sort_by_popularity))) {
            movieQueryUri = Uri.parse(SORT_BY_POPULARITY).buildUpon()
                    .appendQueryParameter(apiKeyLabel,apiKeyValue)
                    .build();
        } else {
            movieQueryUri = Uri.parse(SORT_BY_TOP_RATED).buildUpon()
                    .appendQueryParameter(apiKeyLabel,apiKeyValue)
                    .build();
        }

        try {
            URL weatherQueryUrl = new URL(movieQueryUri.toString());
            Log.v(TAG, "URL: " + weatherQueryUrl);
            return weatherQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //from the Sunshine App
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }


}
