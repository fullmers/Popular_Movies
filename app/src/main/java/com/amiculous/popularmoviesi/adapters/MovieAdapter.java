package com.amiculous.popularmoviesi.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amiculous.popularmoviesi.R;
import com.amiculous.popularmoviesi.data.Movie;
import com.amiculous.popularmoviesi.utils.ImageUtils;
import com.amiculous.popularmoviesi.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sarah on 09/02/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private ArrayList<Movie> mMovies;
    private Context mContext;
    private MovieClickListener mClickListener;
    private int mScreenWidthPx;
    private boolean mIsFavorite;

    public interface MovieClickListener {
        void onMovieClick(Movie movie);
    }

    public MovieAdapter(Context context, MovieClickListener movieClickListener, ArrayList<Movie> movies, int screenWidthPx, boolean isFavorite) {
        Log.d(TAG,"creating MovieAdapter");
        this.mContext = context;
        this.mMovies = movies;
        this.mClickListener = movieClickListener;
        this.mScreenWidthPx = screenWidthPx;
        this.mIsFavorite = isFavorite;
        Log.d(TAG,"mMovies size constructor:" + mMovies.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG,"onCreateViewHolder");
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder");
        if (mIsFavorite) {
            Log.d(TAG,"loading local image file");
            String fileName = ImageUtils.getMoviePosterFileName(mMovies.get(position).getTitle());
            File imageFile = ImageUtils.getImageFile(mContext,fileName);
            Picasso.with(mContext)
                    .load(imageFile)
                    .into(holder.mImagePoster);
        }
        else
            {
            Log.d(TAG,"loading remote image file");
            String posterUrl = NetworkUtils.buildMoviePosterUrl(mMovies.get(position).getPosterPath(), mScreenWidthPx);
            Picasso.with(mContext)
                    .load(posterUrl)
                    .into(holder.mImagePoster);
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG,"mMovies size getCount:" + mMovies.size());
        return mMovies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_movie_poster) ImageView mImagePoster;

        ViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG,"creating ViewHolder");
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickListener.onMovieClick(mMovies.get(adapterPosition));
        }
    }
}