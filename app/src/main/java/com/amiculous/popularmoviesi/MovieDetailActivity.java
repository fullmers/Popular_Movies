package com.amiculous.popularmoviesi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MovieDetailActivity extends AppCompatActivity {

    Movie mMovie;
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mMovie = (Movie) extras.getParcelable(getString(R.string.movie_extra_key));
            Log.d(TAG,mMovie.getTitle());
        }
    }
}
