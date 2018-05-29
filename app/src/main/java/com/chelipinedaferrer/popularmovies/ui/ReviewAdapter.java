package com.chelipinedaferrer.popularmovies.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chelipinedaferrer.popularmovies.R;
import com.chelipinedaferrer.popularmovies.entities.Review;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    private Review[] reviewsData;

    private final ReviewAdapterOnClickHandler clickHandler;

    public interface ReviewAdapterOnClickHandler {
        void onClick(Review review);
    }

    /**
     * Creates a ReviewAdapter.
     */
    public ReviewAdapter(ReviewAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        Review review = reviewsData[position];

        holder.reviewAuthor.setText(review.getAuthor());
        holder.reviewContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if (null == reviewsData) {
            return 0;
        }

        return reviewsData.length;
    }

    /**
     * This method is used to set the reviews on a ReviewAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ReviewAdapter to display it.
     *
     * @param reviewsData The new reviewss data to be displayed.
     */
    public void setReviewsData(Review[] reviewsData) {
        this.reviewsData = reviewsData;
        notifyDataSetChanged();
    }


    /**
     * Cache of the children views for a review list item.
     */
    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.review_author)
        TextView reviewAuthor;
        @BindView(R.id.review_content)
        TextView reviewContent;

        public ReviewAdapterViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Review review = reviewsData[adapterPosition];
            clickHandler.onClick(review);

        }
    }
}
