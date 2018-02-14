package com.amiculous.popularmoviesi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amiculous.popularmoviesi.adapters.MovieAdapter;
import com.amiculous.popularmoviesi.data.Movie;
import com.amiculous.popularmoviesi.loaders.ApiMovieLoader;
import com.amiculous.popularmoviesi.loaders.ProviderMovieLoader;
import com.amiculous.popularmoviesi.utils.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>,
MovieAdapter.MovieClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int API_MOVIE_LOADER = 0;
    private static final int FAVORITES_MOVIE_LOADER = 1;
    private int mScreenWidthPx;
    private SharedPreferences mPrefs;
    private PreferenceChangeListener mPrefChangeListener;
    private String mCurrentSortPref;
    private MovieAdapter mAdapter;
    private ApiMovieLoader mApiMovieLoader;
    private ProviderMovieLoader mProviderMovieLoader;
    private boolean mIsFavorites;

    @BindView(R.id.rvMovies) RecyclerView mMovieRecyclerView;
    @BindView(R.id.progress_spinner) ProgressBar mProgressSpinner;
    @BindView(R.id.text_no_internet) TextView mNoInternetText;
    @BindView(R.id.text_no_favorites) TextView mNoFavoritesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getScreenWidthPx();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefChangeListener = new PreferenceChangeListener();
        mPrefs.registerOnSharedPreferenceChangeListener(mPrefChangeListener);

        mCurrentSortPref = mPrefs.getString(getString(R.string.pref_sort_by_key), "");
        if (mCurrentSortPref.equals(getString(R.string.pref_sort_by_favorites))) {
            mIsFavorites = true;
            getSupportLoaderManager().initLoader(FAVORITES_MOVIE_LOADER, null, MainActivity.this).forceLoad();
        }
        else {
            mIsFavorites = false;
            if (NetworkUtils.isConnectedToInternet(this)) {
            getSupportLoaderManager().initLoader(API_MOVIE_LOADER, null, MainActivity.this).forceLoad();
            } else {
                mNoInternetText.setVisibility(View.VISIBLE);
                mMovieRecyclerView.setVisibility(GONE);
                mProgressSpinner.setVisibility(GONE);
            }
        }
    }

    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            mCurrentSortPref = prefs.getString(key, "");
            if (mCurrentSortPref.equals(getString(R.string.pref_sort_by_favorites))) {
                mIsFavorites = true;
                getSupportLoaderManager().restartLoader(FAVORITES_MOVIE_LOADER, null, MainActivity.this).forceLoad();
            }
            else {
                mIsFavorites = false;
                getSupportLoaderManager().restartLoader(API_MOVIE_LOADER, null, MainActivity.this).forceLoad();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case(API_MOVIE_LOADER): {
                    mNoInternetText.setVisibility(GONE);
                    mMovieRecyclerView.setVisibility(GONE);
                    mNoFavoritesText.setVisibility(GONE);
                    mProgressSpinner.setVisibility(View.VISIBLE);
                    mApiMovieLoader = new ApiMovieLoader(this, mNoInternetText);
                    return mApiMovieLoader;

            } case(FAVORITES_MOVIE_LOADER): {
                mNoInternetText.setVisibility(GONE);
                mMovieRecyclerView.setVisibility(GONE);
                mProgressSpinner.setVisibility(View.VISIBLE);
                mProviderMovieLoader = new ProviderMovieLoader(this);
                return mProviderMovieLoader;
            } default:
                return null;
        }
    }


    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
        mProgressSpinner.setVisibility(GONE);
        int numberOfColumns = 2;
        for (Movie movie: movies) {
            Log.d(TAG,movie.getTitle());
        }
        mAdapter = new MovieAdapter(this, this, movies, mScreenWidthPx, mIsFavorites);
        mMovieRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        mMovieRecyclerView.setAdapter(mAdapter);
        if (movies.size() == 0 && mIsFavorites) {
            mNoFavoritesText.setVisibility(View.VISIBLE);
            mMovieRecyclerView.setVisibility(View.GONE);
        } else {
            mMovieRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent movieDetailIntent = new Intent(MainActivity.this,MovieDetailActivity.class);
        movieDetailIntent.putExtra(getString(R.string.movie_extra_key),movie);
        movieDetailIntent.putExtra(getString(R.string.screen_width_extra_key),mScreenWidthPx);
        startActivity(movieDetailIntent);
    }

    private void getScreenWidthPx() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidthPx = displayMetrics.widthPixels;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPrefs.unregisterOnSharedPreferenceChangeListener(mPrefChangeListener);
    }

  @Override
    protected void onResume() {
        super.onResume();
        boolean hasInternet = NetworkUtils.isConnectedToInternet(this);
        if (hasInternet) {
            mNoInternetText.setVisibility(GONE);
            mMovieRecyclerView.setVisibility(View.VISIBLE);
            if (mApiMovieLoader == null && !mCurrentSortPref.equals(getString(R.string.pref_sort_by_favorites))) {
                getSupportLoaderManager().initLoader(API_MOVIE_LOADER, null, MainActivity.this).forceLoad();
            } else if (mProviderMovieLoader == null && mCurrentSortPref.equals(getString(R.string.pref_sort_by_favorites))) {
                getSupportLoaderManager().initLoader(FAVORITES_MOVIE_LOADER, null, MainActivity.this).forceLoad();
            }
        } else { //has no internet
            //not favorites, nothing to see:
            if (!mCurrentSortPref.equals(getString(R.string.pref_sort_by_favorites))) {
                mNoInternetText.setVisibility(View.VISIBLE);
                mMovieRecyclerView.setVisibility(GONE);
            }
            //is favorites, can view offline:
            else if (mCurrentSortPref.equals(getString(R.string.pref_sort_by_favorites))) {
                mNoInternetText.setVisibility(GONE);
                mMovieRecyclerView.setVisibility(View.VISIBLE);
                getSupportLoaderManager().restartLoader(FAVORITES_MOVIE_LOADER, null, MainActivity.this).forceLoad();
            }
        }
    }
}