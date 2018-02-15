package com.amiculous.popularmoviesi;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amiculous.popularmoviesi.adapters.ReviewAdapter;
import com.amiculous.popularmoviesi.adapters.VideoAdapter;
import com.amiculous.popularmoviesi.data.FavoriteMoviesContract;
import com.amiculous.popularmoviesi.data.FavoriteMoviesContract.FavoritesEntry;
import com.amiculous.popularmoviesi.data.Movie;
import com.amiculous.popularmoviesi.data.MovieExtras;
import com.amiculous.popularmoviesi.data.MovieReview;
import com.amiculous.popularmoviesi.data.MovieVideo;
import com.amiculous.popularmoviesi.loaders.MovieExtrasLoader;
import com.amiculous.popularmoviesi.utils.ImageUtils;
import com.amiculous.popularmoviesi.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieExtras>,
        VideoAdapter.VideoClickListener {

    private Movie mMovie;
    private int mScreenWidth;
    private String mPosterUrl;
    private Uri mUri;
    private MovieExtrasLoader mMovieExtrasLoader;
    private int movieId;
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;
    private String mImageFileName;

    @BindView(R.id.text_movie_title) TextView TvMovieTitle;
    @BindView(R.id.text_release_date) TextView TvReleaseDate;
    @BindView(R.id.text_user_rating) TextView TvUserRating;
    @BindView(R.id.text_overview) TextView TvOverview;
    @BindView(R.id.text_no_internet) TextView TvNoInternet;
    @BindView(R.id.image_movie_poster) ImageView ImageMoviePoster;
    @BindView(R.id.constraint_layout_favorite_data) ConstraintLayout ClFavoriteData;
    @BindView(R.id.linear_layout_requires_internet) LinearLayout LlRequiresInternet;
    @BindView(R.id.chbx_favorite) CheckBox CbFavorite;
    @BindView(R.id.rvTrailers) RecyclerView RvVideos;
    @BindView(R.id.rvReviews) RecyclerView RvReviews;
    @BindView(R.id.text_no_reviews) TextView TvNoReviews;
    @BindView(R.id.text_no_trailers) TextView TvNoTrailers;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mMovie = extras.getParcelable(getString(R.string.movie_extra_key));
            mScreenWidth = extras.getInt(getString(R.string.screen_width_extra_key));
            movieId = mMovie.getId();
            mImageFileName = ImageUtils.getMoviePosterFileName(mMovie.getTitle());
            mUri= FavoriteMoviesContract.FavoritesEntry.buildMovieUriWithId(mMovie.getId());

            setupUI();
        }
    }

    private void setupUI() {
        TvMovieTitle.setText(mMovie.getTitle());
        TvReleaseDate.setText(getReleaseYear());
        TvUserRating.setText(Double.toString(mMovie.getVoteAverage()));
        TvOverview.setText(mMovie.getOverview());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle(mMovie.getTitle());
        boolean isFavorite = isFavorite();
        if (isFavorite) {
            CbFavorite.setChecked(true);
        } else {
            CbFavorite.setChecked(false);
        }

        boolean hasInternet = NetworkUtils.isConnectedToInternet(getApplicationContext());
        if (hasInternet) { //if hasInternet, does not matter if isFavorite
            TvNoInternet.setVisibility(View.GONE);
            ClFavoriteData.setVisibility(View.VISIBLE);
            LlRequiresInternet.setVisibility(View.VISIBLE);
            mPosterUrl = NetworkUtils.buildMoviePosterUrl(mMovie.getPosterPath(),mScreenWidth);
            Picasso.with(this)
                    .load(mPosterUrl)
                    .into(ImageMoviePoster);

            getSupportLoaderManager().initLoader(0, null, MovieDetailActivity.this).forceLoad();
        } else if (isFavorite) { //no internet but is favorite
            String fileName = ImageUtils.getMoviePosterFileName(mMovie.getTitle());
            File imageFile = ImageUtils.getImageFile(this,fileName);
            Picasso.with(this)
                    .load(imageFile)
                    .into(ImageMoviePoster);

            TvNoInternet.setVisibility(View.VISIBLE);
            ClFavoriteData.setVisibility(View.VISIBLE);
            LlRequiresInternet.setVisibility(View.GONE);
        } else { //no internet and not favorite
            TvNoInternet.setVisibility(View.VISIBLE);
            ClFavoriteData.setVisibility(View.INVISIBLE);
            LlRequiresInternet.setVisibility(View.GONE);
        }

        CbFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    if (NetworkUtils.isConnectedToInternet(getApplicationContext()) && mPosterUrl != null) {
                        insertInFavoriteMovies();
                    } else {
                        Toast.makeText(MovieDetailActivity.this,"Cannot Favorite movie while offline",Toast.LENGTH_SHORT).show();
                        ((CheckBox) v).setChecked(false);
                    }
                } else {
                    deleteFromFavoriteMovies();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        //https://stackoverflow.com/questions/26651602/display-back-arrow-on-toolbar-android
        onBackPressed();
        return true;
    }

    public boolean isFavorite() {
        boolean isFavorite = false;
        Cursor cursor = getContentResolver().query(
                mUri,
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
                isFavorite = true;
                cursor.close();
        }

        return isFavorite;
    }


    public void insertInFavoriteMovies() {
        ContentValues contentValues= new ContentValues();
        contentValues.put(FavoritesEntry.COLUMN_MOVIE_ID, mMovie.getId());
        contentValues.put(FavoritesEntry.COLUMN_MOVIE_TITLE, mMovie.getTitle());
        contentValues.put(FavoritesEntry.COLUMN_MOVIE_POSTER_URI, mMovie.getPosterPath());
        contentValues.put(FavoritesEntry.COLUMN_MOVIE_OVERVIEW, mMovie.getOverview());
        contentValues.put(FavoritesEntry.COLUMN_MOVIE_VOTE_AVERAGE, mMovie.getVoteAverage());
        contentValues.put(FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE, mMovie.getReleaseDate());

        getContentResolver().insert(FavoritesEntry.CONTENT_URI, contentValues);

        if (NetworkUtils.isConnectedToInternet(getApplicationContext()) && mPosterUrl != null) {
            Picasso.with(this)
                    .load(mPosterUrl)
                    .into(ImageUtils.picassoImageTarget(this,mImageFileName));
        }
    }

    public void deleteFromFavoriteMovies() {
        getContentResolver().delete(mUri, FavoritesEntry.COLUMN_MOVIE_ID + "=" + mMovie.getId(), null);
        ImageUtils.deleteImageFile(this,mImageFileName);
    }

    private String getReleaseYear() {
        String fullDateString = mMovie.getReleaseDate();
        String[] parts = fullDateString.split("-");
        return parts[0];
    }

    @Override
    public Loader<MovieExtras> onCreateLoader(int id, Bundle args) {
        mMovieExtrasLoader = new MovieExtrasLoader(this, movieId);
        return mMovieExtrasLoader;
    }

    @Override
    public void onLoadFinished(Loader<MovieExtras> loader, MovieExtras movieExtras) {
        ArrayList<MovieVideo> videos = movieExtras.getYoutubeVideos();
        if (videos.size() == 0 ){
            RvVideos.setVisibility(View.GONE);
            TvNoTrailers.setVisibility(View.VISIBLE);
        } else {
            RvVideos.setVisibility(View.VISIBLE);
            TvNoTrailers.setVisibility(View.GONE);
            LinearLayoutManager videoLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            RvVideos.setLayoutManager(videoLayoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                    RvVideos.getContext(),
                    videoLayoutManager.getOrientation());
            RvVideos.addItemDecoration(dividerItemDecoration);
            mVideoAdapter = new VideoAdapter(this, this, videos);
            RvVideos.setAdapter(mVideoAdapter);
            RvVideos.setNestedScrollingEnabled(false);
        }

        ArrayList<MovieReview> reviews = movieExtras.getReviews();
        if (reviews.size() == 0 ){
            RvReviews.setVisibility(View.GONE);
            TvNoReviews.setVisibility(View.VISIBLE);
        } else {
            RvReviews.setVisibility(View.VISIBLE);
            TvNoReviews.setVisibility(View.GONE);
            LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            RvReviews.setLayoutManager(reviewLayoutManager);
            DividerItemDecoration reviewDividerItemDecoration = new DividerItemDecoration(
                    RvReviews.getContext(),
                    reviewLayoutManager.getOrientation());
            RvReviews.addItemDecoration(reviewDividerItemDecoration);
            mReviewAdapter = new ReviewAdapter(this, reviews);
            RvReviews.setAdapter(mReviewAdapter);
            RvReviews.setNestedScrollingEnabled(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieExtras> loader) {

    }

    @Override
    public void onMovieClick(MovieVideo video) {
        Intent launchYouTube = new Intent(Intent.ACTION_VIEW);
        launchYouTube.setData(Uri.parse(video.getYoutubeURL().toString()));
        startActivity(launchYouTube);
    }


    public enum MovieExtraTypes {REVIEWS, VIDEOS}
}
