package com.amiculous.popularmoviesi.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.amiculous.popularmoviesi.data.Movie;
import com.amiculous.popularmoviesi.data.FavoriteMoviesContract.FavoritesEntry;

import java.util.ArrayList;

/**
 * Created by sarah on 13/02/2018.
 */

public class ProviderMovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private final String TAG = ProviderMovieLoader.class.getSimpleName();

    private ArrayList<Movie> mMovies;
    private Cursor mCursor;

    public ProviderMovieLoader(Context context) {
        super(context);
        Uri uri = FavoritesEntry.CONTENT_URI;
        mCursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null);
    }

    @Nullable
    @Override
    public ArrayList<Movie> loadInBackground() {
        return getFavorites();
    }

    @Override
    public void deliverResult(ArrayList<Movie> data) {
        mMovies = data;
        super.deliverResult(data);
    }

    public ArrayList<Movie> getFavorites() {
        int movieTitleIndex= mCursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_TITLE);
        int movieIdIndex= mCursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_ID);
        int moviePosterIndex= mCursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_POSTER_URI);
        int movieBackdropIndex= mCursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_BACKDROP_URI);
        int movieOverviewIndex= mCursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_OVERVIEW);
        int movieVoteAverageIndex= mCursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_VOTE_AVERAGE);
        int movieReleaseDateIndex= mCursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE);

        ArrayList<Movie> favoriteMovies = new ArrayList<>();
        while(mCursor.moveToNext()) {
            String movieTitle = mCursor.getString(movieTitleIndex);
            int movieId = mCursor.getInt(movieIdIndex);
            String moviePoster = mCursor.getString(moviePosterIndex);
            String movieBackdrop = mCursor.getString(movieBackdropIndex);
            String movieOverview = mCursor.getString(movieOverviewIndex);
            double movieVoteAverage = mCursor.getDouble(movieVoteAverageIndex);
            String movieReleaseDate = mCursor.getString(movieReleaseDateIndex);

            Movie movie = new Movie(movieId, movieVoteAverage, movieTitle, moviePoster, movieBackdrop, movieOverview, movieReleaseDate);
            favoriteMovies.add(movie);
            Log.d(TAG,movieTitle + " " + movieId);
        }
        if (mCursor != null) {
            mCursor.close();
        }
        return favoriteMovies;
    }


   @Override
    protected void onStartLoading() {

       if (mMovies != null) {
            deliverResult(mMovies);
        }
    }

}
