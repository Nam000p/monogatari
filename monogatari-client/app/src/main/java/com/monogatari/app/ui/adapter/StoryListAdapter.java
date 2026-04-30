package com.monogatari.app.ui.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.monogatari.app.data.model.story.StoryResponse;
import com.monogatari.app.databinding.ItemStoryListBinding;

import java.util.ArrayList;
import java.util.List;

public class StoryListAdapter extends RecyclerView.Adapter<StoryListAdapter.StoryListViewHolder> {

    private List<StoryResponse> stories = new ArrayList<>();
    private final OnStoryClickListener listener;

    public interface OnStoryClickListener {
        void onStoryClick(StoryResponse story);
    }

    public StoryListAdapter(OnStoryClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setStories(List<StoryResponse> newStories) {
        this.stories = newStories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStoryListBinding binding = ItemStoryListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StoryListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryListViewHolder holder, int position) {
        holder.bind(stories.get(position));
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    class StoryListViewHolder extends RecyclerView.ViewHolder {
        private final ItemStoryListBinding binding;

        public StoryListViewHolder(ItemStoryListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(StoryResponse story) {
            binding.tvTitle.setText(story.getTitle() != null ? story.getTitle() : "Unknown Title");

            String rating = story.getAverageRating() != null ? String.valueOf(story.getAverageRating()) : "N/A";
            String type = story.getType() != null ? story.getType().name() : "";
            binding.tvRatingAndType.setText("★ " + rating + "  •  " + type);

            if (story.getGenres() != null && !story.getGenres().isEmpty()) {
                binding.tvGenres.setText(TextUtils.join(", ", story.getGenres()));
            } else {
                binding.tvGenres.setText("");
            }

            binding.tvDescription.setText(story.getDescription() != null ? story.getDescription() : "No description available.");

            Glide.with(binding.getRoot().getContext())
                    .load(story.getCoverUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivCover);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStoryClick(story);
                }
            });
        }
    }
}