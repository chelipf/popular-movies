package com.chelipinedaferrer.popularmovies.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chelipinedaferrer.popularmovies.R;
import com.chelipinedaferrer.popularmovies.entities.Trailer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {
    private Trailer[] trailersData;

    private final TrailerAdapterOnClickHandler clickHandler;

    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer trailer);
    }

    /**
     * Creates a TrailerAdapter.
     */
    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        Trailer trailer = trailersData[position];

        holder.trailerName.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        if (null == trailersData) {
            return 0;
        }

        return trailersData.length;
    }

    /**
     * This method is used to set the trailers on a TrailerAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new TrailerAdapter to display it.
     *
     * @param trailersData The new trailers data to be displayed.
     */
    public void setTrailersData(Trailer[] trailersData) {
        this.trailersData = trailersData;
        notifyDataSetChanged();
    }


    /**
     * Cache of the children views for a trailer list item.
     */
    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.play_trailer_image)
        ImageView playTrailerImage;
        @BindView(R.id.trailer_name)
        TextView trailerName;

        public TrailerAdapterViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Trailer trailer = trailersData[adapterPosition];
            clickHandler.onClick(trailer);

        }
    }
}
