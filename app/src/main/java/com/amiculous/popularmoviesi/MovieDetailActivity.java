package com.amiculous.popularmoviesi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.amiculous.popularmoviesi.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    Movie mMovie;
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    @BindView(R.id.text_movie_title) TextView TvMovieTitle;
    @BindView(R.id.text_release_date) TextView TvReleaseDate;
    @BindView(R.id.text_user_rating) TextView TvUserRating;
    @BindView(R.id.text_overview) TextView TvOverview;

    @BindView(R.id.image_movie_poster) ImageView ImageMoviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mMovie = extras.getParcelable(getString(R.string.movie_extra_key));
            setupUI();
        }
    }

    private void setupUI() {
        TvMovieTitle.setText(mMovie.getTitle());
        TvReleaseDate.setText(getReleaseYear());
        TvUserRating.setText(Double.toString(mMovie.getVoteAverage()));
        TvOverview.setText(mMovie.getOverview());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        String posterUrl = NetworkUtils.buildMoviePosterUrl(mMovie.getPosterPath(),width);
        Picasso.with(this)
                .load(posterUrl)
                .into(ImageMoviePoster);
    }

    private String getReleaseYear() {
        String fullDateString = mMovie.getReleaseDate();
        String[] parts = fullDateString.split("-");
        return parts[0];
    }
}
