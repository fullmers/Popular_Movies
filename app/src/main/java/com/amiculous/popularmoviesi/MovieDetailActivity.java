package com.amiculous.popularmoviesi;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.amiculous.popularmoviesi.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieDetailActivity extends AppCompatActivity {

    private Movie mMovie;
    private int mScreenWidth;
    private Uri mUri;
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    @BindView(R.id.text_movie_title) TextView TvMovieTitle;
    @BindView(R.id.text_release_date) TextView TvReleaseDate;
    @BindView(R.id.text_user_rating) TextView TvUserRating;
    @BindView(R.id.text_overview) TextView TvOverview;
    @BindView(R.id.text_no_internet) TextView TvNoInternet;
    @BindView(R.id.image_movie_poster) ImageView ImageMoviePoster;
    @BindView(R.id.constraint_layout_has_internet) ConstraintLayout ClHasInternet;
    @BindView(R.id.chbx_favorite) CheckBox CbFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mMovie = extras.getParcelable(getString(R.string.movie_extra_key));
            mScreenWidth = extras.getInt(getString(R.string.screen_width_extra_key));
            mUri = getIntent().getData();
            setupUI();
        }
    }

    private void setupUI() {
        TvMovieTitle.setText(mMovie.getTitle());
        TvReleaseDate.setText(getReleaseYear());
        TvUserRating.setText(Double.toString(mMovie.getVoteAverage()));
        TvOverview.setText(mMovie.getOverview());

        if (NetworkUtils.isConnectedToInternet(getApplicationContext())) {
            TvNoInternet.setVisibility(View.GONE);
            ClHasInternet.setVisibility(View.VISIBLE);
            String posterUrl = NetworkUtils.buildMoviePosterUrl(mMovie.getPosterPath(),mScreenWidth);
            Picasso.with(this)
                    .load(posterUrl)
                    .into(ImageMoviePoster);
        } else {
            TvNoInternet.setVisibility(View.VISIBLE);
            ClHasInternet.setVisibility(View.INVISIBLE);
        }

        if (isFavorite()) {
            CbFavorite.setChecked(true);
        } else {
            CbFavorite.setChecked(false);
        }

        CbFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    insertInFavoriteMovies();
                } else {
                    deleteFromFavoriteMovies();
                }
            }
        });
    }

    public boolean isFavorite() {
        boolean isFavorite = false;
        Cursor cursor = getContentResolver().query(
                mUri,
                null,
                null,
                null,
                null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                isFavorite = true;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isFavorite;
    }


    //TODO
    public void insertInFavoriteMovies() {}
    //TODO
    public void deleteFromFavoriteMovies() {}


    private String getReleaseYear() {
        String fullDateString = mMovie.getReleaseDate();
        String[] parts = fullDateString.split("-");
        return parts[0];
    }
}
