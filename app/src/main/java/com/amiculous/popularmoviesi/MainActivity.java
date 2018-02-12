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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

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

    @BindView(R.id.rvMovies) RecyclerView mMovieRecyclerView;
    @BindView(R.id.progress_spinner) ProgressBar mProgressSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getScreenWidthPx();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefChangeListener = new PreferenceChangeListener();
        mPrefs.registerOnSharedPreferenceChangeListener(mPrefChangeListener);

        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this).forceLoad();
    }


    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key.equals(getString(R.string.pref_sort_by_key))) {
                getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER,null,MainActivity.this).forceLoad();
                mAdapter.notifyDataSetChanged();
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
            mProgressSpinner.setVisibility(View.VISIBLE);
            return new MovieLoader(this);
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

}
