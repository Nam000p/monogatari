package com.monogatari.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.monogatari.app.data.model.story.StoryResponse;
import com.monogatari.app.data.model.enums.StoryStatus;
import com.monogatari.app.databinding.ItemStoryBinding;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private List<StoryResponse> storyList;
    private OnStoryClickListener listener;

    public interface OnStoryClickListener {
        void onStoryClick(StoryResponse story);
    }

    // Required Constructor
    public StoryAdapter(List<StoryResponse> storyList) {
        this.storyList = storyList;
    }

    public void setOnStoryClickListener(OnStoryClickListener listener) {
        this.listener = listener;
    }

    public void setStories(List<StoryResponse> stories) {
        this.storyList = stories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStoryBinding binding = ItemStoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new StoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        StoryResponse story = storyList.get(position);
        holder.bind(story);
    }

    @Override
    public int getItemCount() {
        return storyList != null ? storyList.size() : 0;
    }

    class StoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemStoryBinding binding;

        public StoryViewHolder(ItemStoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onStoryClick(storyList.get(position));
                }
            });
        }

        public void bind(StoryResponse story) {
            binding.tvStoryTitle.setText(story.getTitle());
            binding.tvStoryAuthor.setText(story.getAuthorName());
            binding.tvStoryDescription.setText(story.getDescription());

            if (story.getStatus() != null) {
                String statusLabel = story.getStatus() == StoryStatus.ONGOING ? "Ongoing" : "Completed";
                binding.tvStatus.setText(statusLabel);
            }

            if (story.getAverageRating() != null) {
                binding.tvRating.setText(String.format("⭐ %.1f", story.getAverageRating()));
            } else {
                binding.tvRating.setText("⭐ 0.0");
            }

            Glide.with(itemView.getContext())
                    .load(story.getCoverUrl())
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(binding.ivStoryCover);
        }
    }
}