package com.amiculous.popularmoviesi.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amiculous.popularmoviesi.R;
import com.amiculous.popularmoviesi.data.MovieReview;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sarah on 14/02/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>  {

    private ArrayList<MovieReview> mReviews;
    private Context mContext;

    public ReviewAdapter(Context context, ArrayList<MovieReview> reviews) {
        this.mContext = context;
        this.mReviews = reviews;
    }


    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, int position) {
        String reviewAuthor = mReviews.get(position).getAuthor();
        String reviewContent = mReviews.get(position).getContent();
        holder.mAuthor.setText(reviewAuthor);
        holder.mContent.setText(reviewContent);
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        @BindView(R.id.text_author) TextView mAuthor;
        @BindView(R.id.text_review_content) TextView mContent;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
