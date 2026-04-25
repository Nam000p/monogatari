package com.monogatari.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monogatari.app.data.model.rating.RatingResponse;
import com.monogatari.app.databinding.ItemRatingBinding;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {
    private List<RatingResponse> ratingList;

    public RatingAdapter(List<RatingResponse> ratingList) {
        this.ratingList = ratingList;
    }

    public void setRatings(List<RatingResponse> ratings) {
        this.ratingList = ratings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRatingBinding binding = ItemRatingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new RatingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        holder.bind(ratingList.get(position));
    }

    @Override
    public int getItemCount() {
        return ratingList != null ? ratingList.size() : 0;
    }

    static class RatingViewHolder extends RecyclerView.ViewHolder {
        private final ItemRatingBinding binding;

        public RatingViewHolder(ItemRatingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(RatingResponse rating) {
            binding.tvUsername.setText(rating.getUsername());
            binding.tvScore.setText("⭐ " + rating.getScore());

            if (rating.getReview() != null && !rating.getReview().isEmpty()) {
                binding.tvComment.setVisibility(android.view.View.VISIBLE);
                binding.tvComment.setText(rating.getReview());
            } else {
                binding.tvComment.setVisibility(android.view.View.GONE);
            }
        }
    }
}