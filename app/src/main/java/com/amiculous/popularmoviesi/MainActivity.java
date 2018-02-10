package com.amiculous.popularmoviesi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>,
MovieAdapter.MovieClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ID_MOVIE_LOADER = 0;
    private int mScreenWidthPx;

    @BindView(R.id.rvMovies) RecyclerView mMovieRecyclerView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getScreenWidthPx();
        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this).forceLoad();
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
            return new MovieLoader(this);
        } else return null;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
        for (Movie movie: movies) {
            String title = movie.getTitle();
            Log.d(TAG,title);
        }
        int numberOfColumns = 2;
        mMovieRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        MovieAdapter mAdapter = new MovieAdapter(this, this, movies, mScreenWidthPx);
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
}
