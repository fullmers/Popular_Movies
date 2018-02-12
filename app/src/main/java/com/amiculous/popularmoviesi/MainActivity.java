package com.amiculous.popularmoviesi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
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

import com.amiculous.popularmoviesi.data.FavoriteMoviesContract;
import com.amiculous.popularmoviesi.utils.JsonUtils;
import com.amiculous.popularmoviesi.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>,
MovieAdapter.MovieClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ID_MOVIE_LOADER = 0;
    private int mScreenWidthPx;
    private SharedPreferences mPrefs;
    private PreferenceChangeListener mPrefChangeListener;
    private MovieAdapter mAdapter;
    private MovieLoader mMovieLoader;

    @BindView(R.id.rvMovies) RecyclerView mMovieRecyclerView;
    @BindView(R.id.progress_spinner) ProgressBar mProgressSpinner;
    @BindView(R.id.text_no_internet) TextView mNoInternetText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getScreenWidthPx();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefChangeListener = new PreferenceChangeListener();
        mPrefs.registerOnSharedPreferenceChangeListener(mPrefChangeListener);

        if (NetworkUtils.isConnectedToInternet(this)) {
            mNoInternetText.setVisibility(View.GONE);
            getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this).forceLoad();
        } else {
            mNoInternetText.setVisibility(View.VISIBLE);
        }
    }

    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key.equals(getString(R.string.pref_sort_by_key))) {
                if (NetworkUtils.isConnectedToInternet(MainActivity.this)) {
                    getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER, null, MainActivity.this).forceLoad();
                } else {
                    Log.d(TAG,"No internet");
                }
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
        if (id == ID_MOVIE_LOADER) {
            if (NetworkUtils.isConnectedToInternet(this)) {
                mNoInternetText.setVisibility(View.GONE);
                mMovieRecyclerView.setVisibility(View.GONE);
                mProgressSpinner.setVisibility(View.VISIBLE);
                mMovieLoader = new MovieLoader(this, mNoInternetText);
                return mMovieLoader;
            } else {
                mNoInternetText.setVisibility(View.VISIBLE);
                mMovieRecyclerView.setVisibility(View.GONE);
                mProgressSpinner.setVisibility(View.GONE);
                return null;
            }
        } else return null;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
        mProgressSpinner.setVisibility(View.GONE);
        int numberOfColumns = 2;
        mMovieRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        mAdapter = new MovieAdapter(this, this, movies, mScreenWidthPx);
        mMovieRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent movieDetailIntent = new Intent(MainActivity.this,MovieDetailActivity.class);
        movieDetailIntent.putExtra(getString(R.string.movie_extra_key),movie);
        movieDetailIntent.putExtra(getString(R.string.screen_width_extra_key),mScreenWidthPx);
        Uri movieUri = FavoriteMoviesContract.FavoritesEntry.buildMovieUriWithId(movie.getId());
        Log.d(TAG,"selected movie Uri:" + movieUri.toString());
        movieDetailIntent.setData(movieUri);
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
        Log.d(TAG,"onResume");
        if (NetworkUtils.isConnectedToInternet(this)) {
            mNoInternetText.setVisibility(View.GONE);
            mMovieRecyclerView.setVisibility(View.VISIBLE);
            if (mMovieLoader == null) {
                getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, MainActivity.this).forceLoad();
            }
        } else {
            mNoInternetText.setVisibility(View.VISIBLE);
            mMovieRecyclerView.setVisibility(View.GONE);
        }
    }

    public static class MovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {

        private final String TAG = MovieLoader.class.getSimpleName();

        private ArrayList<Movie> mMovies;
        private URL mUrl;
        private TextView mNoInternetText;


        public MovieLoader(Context context, TextView noInternetText) {
            super(context);
            mUrl = NetworkUtils.buildUrl(context);
            mNoInternetText = noInternetText;
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
                if (NetworkUtils.isConnectedToInternet(getContext())) {
                    mNoInternetText.setVisibility(View.GONE);
                    deliverResult(mMovies);
                } else {
                    mNoInternetText.setVisibility(View.VISIBLE);
                }
            }

            if (takeContentChanged() || mMovies == null) {
                forceLoad();
            }
        }
    }


}
