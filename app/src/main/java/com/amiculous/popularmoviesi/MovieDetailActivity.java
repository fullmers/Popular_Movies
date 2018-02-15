package com.amiculous.popularmoviesi;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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


public class MovieDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<MovieExtras>, VideoAdapter.VideoClickListener {

    private Movie mMovie;
    private int mScreenWidth;
    private String mPosterUrl;
    private String mBackdropUrl;
    private Uri mUri;
    private MovieExtrasLoader mMovieExtrasLoader;
    private int movieId;
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;
    private String mPosterFileName;
    private String mBackdropFileName;
    private boolean mIsFavorite;
    private int mScrollPosition;
    private Bundle mSavedInstanceState;

    @BindView(R.id.text_release_date) TextView TvReleaseDate;
    @BindView(R.id.text_user_rating) TextView TvUserRating;
    @BindView(R.id.text_overview) TextView TvOverview;
    @BindView(R.id.text_no_internet) TextView TvNoInternet;
    @BindView(R.id.image_backdrop) ImageView ImageBackdrop;
    @BindView(R.id.constraint_layout_favorite_data) ConstraintLayout ClFavoriteData;
    @BindView(R.id.linear_layout_requires_internet) LinearLayout LlRequiresInternet;
    @BindView(R.id.rvTrailers) RecyclerView RvVideos;
    @BindView(R.id.rvReviews) RecyclerView RvReviews;
    @BindView(R.id.text_no_reviews) TextView TvNoReviews;
    @BindView(R.id.text_no_trailers) TextView TvNoTrailers;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.nested_scroll_view) NestedScrollView nestedScrollView;
    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        mSavedInstanceState = savedInstanceState;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mMovie = extras.getParcelable(getString(R.string.movie_extra_key));
            mScreenWidth = extras.getInt(getString(R.string.screen_width_extra_key));
            movieId = mMovie.getId();
            mPosterFileName = ImageUtils.getMovieImageFileName(mMovie.getTitle(), ImageUtils.ImageType.POSTER);
            mBackdropFileName = ImageUtils.getMovieImageFileName(mMovie.getTitle(), ImageUtils.ImageType.BACKDROP);
            mUri= FavoriteMoviesContract.FavoritesEntry.buildMovieUriWithId(mMovie.getId());

            setupUI();
        }
    }

    private void setupUI() {
        TvReleaseDate.setText(getReleaseYear());
        TvUserRating.setText(Double.toString(mMovie.getVoteAverage()));
        TvOverview.setText(mMovie.getOverview());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle(mMovie.getTitle());
        collapsingToolbar.setTitle(mMovie.getTitle());
        mIsFavorite = isFavorite();
        if (mIsFavorite) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
        } else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
        }

        boolean hasInternet = NetworkUtils.isConnectedToInternet(getApplicationContext());
        if (hasInternet) { //if hasInternet, does not matter if isFavorite
            TvNoInternet.setVisibility(View.GONE);
            ClFavoriteData.setVisibility(View.VISIBLE);
            LlRequiresInternet.setVisibility(View.VISIBLE);
            mPosterUrl = NetworkUtils.buildMovieImageUrl(mMovie.getPosterPath(),mScreenWidth);
            mBackdropUrl = NetworkUtils.buildMovieImageUrl(mMovie.getBackdropPath(),mScreenWidth);

            Picasso.with(this)
                    .load(mBackdropUrl)
                    .error(R.drawable.missing_image)
                    .into(ImageBackdrop);

            getSupportLoaderManager()
                    .initLoader(0, null, MovieDetailActivity.this).forceLoad();
        } else if (mIsFavorite) { //no internet but is favorite
            String backdropFileName = ImageUtils.getMovieImageFileName(mMovie.getTitle(), ImageUtils.ImageType.BACKDROP);
            File backdropFile = ImageUtils.getImageFile(this,backdropFileName);
            Picasso.with(this)
                    .load(backdropFile)
                    .error(R.drawable.missing_image)
                    .into(ImageBackdrop);

            TvNoInternet.setVisibility(View.VISIBLE);
            ClFavoriteData.setVisibility(View.VISIBLE);
            LlRequiresInternet.setVisibility(View.GONE);
        } else { //no internet and not favorite
            TvNoInternet.setVisibility(View.VISIBLE);
            ClFavoriteData.setVisibility(View.INVISIBLE);
            LlRequiresInternet.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsFavorite) {
                    if (NetworkUtils.isConnectedToInternet(getApplicationContext()) && mPosterUrl != null) {
                        insertInFavoriteMovies();
                        ((FloatingActionButton) v).setImageDrawable(
                                getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
                        mIsFavorite = true;
                    } else {
                        Toast.makeText(MovieDetailActivity.this,"Cannot Favorite movie while offline",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    deleteFromFavoriteMovies();
                    ((FloatingActionButton) v).setImageDrawable(
                            getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
                    mIsFavorite = false;
                }
            }
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                mScrollPosition = scrollY;
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null && nestedScrollView != null) {
            outState.putInt(getString(R.string.last_position_key),mScrollPosition);
        }
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
        contentValues.put(FavoritesEntry.COLUMN_MOVIE_BACKDROP_URI, mMovie.getBackdropPath());
        contentValues.put(FavoritesEntry.COLUMN_MOVIE_OVERVIEW, mMovie.getOverview());
        contentValues.put(FavoritesEntry.COLUMN_MOVIE_VOTE_AVERAGE, mMovie.getVoteAverage());
        contentValues.put(FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE, mMovie.getReleaseDate());

        getContentResolver().insert(FavoritesEntry.CONTENT_URI, contentValues);

        if (NetworkUtils.isConnectedToInternet(getApplicationContext())) {
            if (mPosterUrl != null) {
                Picasso.with(this)
                        .load(mPosterUrl)
                        .error(R.drawable.missing_image)
                        .into(ImageUtils.picassoImageTarget(this, mPosterFileName));
            }
            if (mBackdropUrl != null) {
                Picasso.with(this)
                        .load(mBackdropUrl)
                        .error(R.drawable.missing_image)
                        .into(ImageUtils.picassoImageTarget(this, mBackdropFileName));
            }
        }

        Snackbar insertionSnackbar =
                Snackbar.make(coordinatorLayout,R.string.added_to_favorites,Snackbar.LENGTH_SHORT);
        insertionSnackbar.show();
    }

    public void deleteFromFavoriteMovies() {
        getContentResolver()
                .delete(mUri, FavoritesEntry.COLUMN_MOVIE_ID + "=" + mMovie.getId(), null);
        ImageUtils.deleteImageFile(this, mPosterFileName);
        ImageUtils.deleteImageFile(this, mBackdropFileName);
        Snackbar deletionSnackbar =
                Snackbar.make(coordinatorLayout,R.string.removed_from_favorites,Snackbar.LENGTH_SHORT);
        deletionSnackbar.show();
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
            LinearLayoutManager videoLayoutManager =
                    new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
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
            LinearLayoutManager reviewLayoutManager =
                    new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            RvReviews.setLayoutManager(reviewLayoutManager);
            DividerItemDecoration reviewDividerItemDecoration = new DividerItemDecoration(
                    RvReviews.getContext(),
                    reviewLayoutManager.getOrientation());
            RvReviews.addItemDecoration(reviewDividerItemDecoration);
            mReviewAdapter = new ReviewAdapter(this, reviews);
            RvReviews.setAdapter(mReviewAdapter);
            RvReviews.setNestedScrollingEnabled(false);
        }

        if (mSavedInstanceState != null && nestedScrollView != null) {
            int lastPosition = mSavedInstanceState.getInt(getString(R.string.last_position_key));
            nestedScrollView.scrollTo(0,lastPosition);
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
