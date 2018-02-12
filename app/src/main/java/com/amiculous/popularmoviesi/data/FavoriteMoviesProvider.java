package com.amiculous.popularmoviesi.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.amiculous.popularmoviesi.data.FavoriteMoviesContract.FavoritesEntry;

/**
 * Created by sarah on 12/02/2018.
 */

public class FavoriteMoviesProvider extends ContentProvider {

    public static final int CODE_FAVORITE_MOVIES = 100;
    public static final int CODE_FAVORITE_MOVIE_DETAILS = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoriteMoviesDbHelper mMoviesDbHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteMoviesContract.CONTENT_AUTHORITY;

        //all movies:
        matcher.addURI(authority, FavoriteMoviesContract.PATH_FAVORITE_MOVIES, CODE_FAVORITE_MOVIES);

        //details about single movie:
        matcher.addURI(authority, FavoriteMoviesContract.PATH_FAVORITE_MOVIES + "/#", CODE_FAVORITE_MOVIE_DETAILS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMoviesDbHelper = new FavoriteMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE_MOVIES: {
                //load only title and id for all favorites
                String[] mProjection = {
                        FavoritesEntry._ID,
                        FavoritesEntry.COLUMN_MOVIE_ID,
                        FavoritesEntry.COLUMN_MOVIE_TITLE
                };
                cursor = mMoviesDbHelper.getReadableDatabase().query(
                        FavoritesEntry.TABLE_NAME,
                        mProjection,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                break;
            }

            case CODE_FAVORITE_MOVIE_DETAILS: {
                //load all details for a single movie
                String id = uri.getLastPathSegment();
                String mSelection = FavoritesEntry._ID + "=?";
                String[] mSelectionArgs = new String[]{id};

                cursor = mMoviesDbHelper.getReadableDatabase().query(
                        FavoritesEntry.TABLE_NAME,
                        null, //when accessing a single movie, you want all columns for that entry
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        null);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            //called when user favorites a movie
            case CODE_FAVORITE_MOVIES:
                //URI: content://<authority>/favorite_movies
                long id = db.insert(FavoritesEntry.TABLE_NAME,
                        null,
                        contentValues);

                if (id != -1) {
                    returnUri = ContentUris.withAppendedId(FavoritesEntry.CONTENT_URI,id);
                } else {
                    throw new android.database.SQLException("failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri.toString());
        }
            getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int favoritesDeleted;

        switch (match) {
            //called when user un-favorites a movie
            case CODE_FAVORITE_MOVIE_DETAILS:
                //URI: content://<authority>/favorite_movies/#
                String movieId = uri.getLastPathSegment();
                String mSelection = FavoritesEntry._ID + "=?";
                String[] mSelectionArgs = new String[]{movieId};
                favoritesDeleted = db.delete(FavoritesEntry.TABLE_NAME,
                        mSelection,
                        mSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri.toString());
        }

        if (favoritesDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return favoritesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
