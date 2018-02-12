package com.amiculous.popularmoviesi.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.amiculous.popularmoviesi.data.FavoriteMoviesContract.FavoritesEntry;

/**
 * Created by sarah on 12/02/2018.
 */

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favorite_movies.db";

    private static final int DATABASE_VERSION = 1;

    public FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_FAVORITES_TABLE =
            "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                    FavoritesEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FavoritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                    FavoritesEntry.COLUMN_MOVIE_TITLE   + " TEXT NOT NULL, " +
                    FavoritesEntry.COLUMN_MOVIE_POSTER_URI   + " TEXT NOT NULL, " +
                    FavoritesEntry.COLUMN_MOVIE_OVERVIEW   + " TEXT NOT NULL, " +
                    FavoritesEntry.COLUMN_MOVIE_VOTE_AVERAGE + " REAL NOT NULL, " +
                    FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE    + " TEXT NOT NULL" +
                    ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
