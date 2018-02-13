package com.amiculous.popularmoviesi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.amiculous.popularmoviesi.BuildConfig;
import com.amiculous.popularmoviesi.MovieDetailActivity;
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

    private static final String API_KEY_VALUE = BuildConfig.API_KEY;
    private static final String API_KEY_LABEL = "api_key";

    private static final String SORT_BY_POPULARITY = "https://api.themoviedb.org/3/movie/popular";
    private static final String SORT_BY_TOP_RATED = "https://api.themoviedb.org/3/movie/top_rated";
    private static final String MOVIE_BASE = "https://api.themoviedb.org/3/movie/";
    private static final String REVIEW = "/reviews";
    private static final String VIDEOS = "/videos";


    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_WIDTH_92 = "w92";
    private static final String IMAGE_WIDTH_154 = "w154";
    private static final String IMAGE_WIDTH_185 = "w185";
    private static final String IMAGE_WIDTH_342 = "w342";
    private static final String IMAGE_WIDTH_500 = "w500";
    private static final String IMAGE_WIDTH_780 = "w780";

    public static String buildMoviePosterUrl(String posterPath, int deviceWidthPx) {
        //use the image width that is less than half of the screen width, rounding down
        //ie, the image will either be half the screen width or slightly less
        if (deviceWidthPx >= 1560) {
            return IMAGE_BASE_URL + IMAGE_WIDTH_780 + posterPath;
        } else if (deviceWidthPx < 1560 && deviceWidthPx >= 1000) {
            return IMAGE_BASE_URL + IMAGE_WIDTH_500 + posterPath;
        } else if (deviceWidthPx < 1000 && deviceWidthPx >= 684) {
            return IMAGE_BASE_URL + IMAGE_WIDTH_342 + posterPath;
        } else if (deviceWidthPx < 1000 && deviceWidthPx >= 684) {
            return IMAGE_BASE_URL + IMAGE_WIDTH_342 + posterPath;
        } else if (deviceWidthPx < 684 && deviceWidthPx >= 370) {
            return IMAGE_BASE_URL + IMAGE_WIDTH_185 + posterPath;
        } else if (deviceWidthPx < 370 && deviceWidthPx >= 308) {
            return IMAGE_BASE_URL + IMAGE_WIDTH_154 + posterPath;
        } else { //deviceWidthPx < 308
                return IMAGE_BASE_URL + IMAGE_WIDTH_92 + posterPath;
        }
    }

    public static URL buildExtrasUrl(int movieId, MovieDetailActivity.MovieExtraTypes movieExtraTypes) {
        Uri extrasQueryUri;
        String uriString = "";
        switch (movieExtraTypes) {
            case REVIEWS: {
                uriString = MOVIE_BASE + movieId + REVIEW;
                break;
            }
            case VIDEOS: {
                uriString = MOVIE_BASE + movieId + VIDEOS;
                break;
            }
        }
        extrasQueryUri = Uri.parse(uriString).buildUpon()
                    .appendQueryParameter(API_KEY_LABEL, API_KEY_VALUE)
                    .build();
        try {
            URL extrasQueryUrl = new URL(extrasQueryUri.toString());
            Log.v(TAG, "MovieExtras URL: " + extrasQueryUrl);
            return extrasQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL buildUrl(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForSortOrder = context.getString(R.string.pref_sort_by_key);
        String defaultSortOrder = context.getString(R.string.pref_sort_by_popularity);
        String sortOrder = sp.getString(keyForSortOrder, defaultSortOrder);

        Uri movieQueryUri;
        if (sortOrder.equals(context.getString(R.string.pref_sort_by_popularity))) {
            movieQueryUri = Uri.parse(SORT_BY_POPULARITY).buildUpon()
                    .appendQueryParameter(API_KEY_LABEL, API_KEY_VALUE)
                    .build();
        } else {
            movieQueryUri = Uri.parse(SORT_BY_TOP_RATED).buildUpon()
                    .appendQueryParameter(API_KEY_LABEL, API_KEY_VALUE)
                    .build();
        }

        try {
            URL movieQueryUrl = new URL(movieQueryUri.toString());
            Log.v(TAG, "URL: " + movieQueryUrl);
            return movieQueryUrl;
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
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

}
