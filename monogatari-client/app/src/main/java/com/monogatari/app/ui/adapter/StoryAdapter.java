package com.monogatari.app.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.monogatari.app.data.model.story.StoryResponse;
import com.monogatari.app.databinding.ItemStoryPosterBinding;

import java.util.ArrayList;
import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private List<StoryResponse> stories = new ArrayList<>();
    private final OnStoryClickListener listener;

    public interface OnStoryClickListener {
        void onStoryClick(StoryResponse story);
    }

    public StoryAdapter(OnStoryClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setStories(List<StoryResponse> newStories) {
        this.stories = newStories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStoryPosterBinding binding = ItemStoryPosterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        holder.bind(stories.get(position));
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    class StoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemStoryPosterBinding binding;

        public StoryViewHolder(ItemStoryPosterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(StoryResponse story) {
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