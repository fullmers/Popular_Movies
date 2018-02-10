package com.amiculous.popularmoviesi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amiculous.popularmoviesi.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sarah on 09/02/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private ArrayList<Movie> mMovies;
    private Context mContext;
    private MovieClickListener mClickListener;
    private int mScreenWidthPx;

    public interface MovieClickListener {
        void onMovieClick(Movie movie);
    }

    public MovieAdapter(Context context, MovieClickListener movieClickListener, ArrayList<Movie> movies, int screenWidthPx) {
        this.mContext = context;
        this.mMovies = movies;
        this.mClickListener = movieClickListener;
        this.mScreenWidthPx = screenWidthPx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String posterUrl = NetworkUtils.buildMoviePosterUrl(mMovies.get(position).getPosterPath(),mScreenWidthPx);
        Picasso.with(mContext)
                .load(posterUrl)
                .into(holder.mImagePoster);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_movie_poster) ImageView mImagePoster;

        ViewHolder(View itemView) {
            super(itemView);
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