package com.amiculous.popularmoviesi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    Movie mMovie;
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    @BindView(R.id.text_movie_title) TextView TvMovieTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mMovie = (Movie) extras.getParcelable(getString(R.string.movie_extra_key));
            TvMovieTitle.setText(mMovie.getTitle());
            Log.d(TAG,mMovie.getTitle());
        }
    }
}
