package com.amiculous.popularmoviesi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>,
MovieAdapter.MovieClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ID_MOVIE_LOADER = 0;

    RecyclerView movieRecyclerView;
    MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movieRecyclerView = (RecyclerView) findViewById(R.id.rvMovies);
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
        movieRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new MovieAdapter(this, this, movies);
        movieRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

    @Override
    public void onMovieClick(Movie movie) {
        Log.d(TAG, "You clicked " + movie.getTitle());
    }
}
