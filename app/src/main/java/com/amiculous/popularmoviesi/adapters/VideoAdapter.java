package com.amiculous.popularmoviesi.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amiculous.popularmoviesi.R;
import com.amiculous.popularmoviesi.data.MovieVideo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sarah on 14/02/2018.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>  {

    private ArrayList<MovieVideo> mVideos;
    private Context mContext;
    private VideoClickListener mClickListener;

    public interface VideoClickListener {
        void onMovieClick(MovieVideo video);
    }

    public VideoAdapter(Context context, VideoClickListener videoClickListener, ArrayList<MovieVideo> videos) {
        this.mContext = context;
        this.mVideos = videos;
        this.mClickListener = videoClickListener;
    }


    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_trailer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoAdapter.ViewHolder holder, int position) {
        String videoTitle = mVideos.get(position).getName();
        holder.mTitle.setText(videoTitle);
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.text_video_title) TextView mTitle;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickListener.onMovieClick(mVideos.get(adapterPosition));
        }
    }
}
