package com.amiculous.popularmoviesi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sarah on 09/02/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private ArrayList<Movie> mMovies;
    private Context mContext;
    private MovieClickListener mClickListener;

    public interface MovieClickListener {
        void onMovieClick(Movie movie);
    }

    public MovieAdapter(Context context, MovieClickListener movieClickListener, ArrayList<Movie> movies) {
        this.mContext = context;
        this.mMovies = movies;
        this.mClickListener = movieClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = mMovies.get(position).getTitle();
        holder.myTextView.setText(title);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = (TextView) itemView.findViewById(R.id.text_movie_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickListener.onMovieClick(mMovies.get(adapterPosition));
        }
    }
}