package com.monogatari.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.monogatari.app.databinding.ItemStoryPosterBinding;
import com.monogatari.app.data.model.story.StoryResponse;
import java.util.ArrayList;
import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private List<StoryResponse> stories = new ArrayList<>();
    private final OnStoryClickListener listener;

    public interface OnStoryClickListener {
        void onStoryClick(StoryResponse story);
    }

    public StoryAdapter(OnStoryClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<StoryResponse> newStories) {
        this.stories = newStories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStoryPosterBinding binding = ItemStoryPosterBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StoryResponse story = stories.get(position);

        holder.itemView.setOnClickListener(v -> listener.onStoryClick(story));
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemStoryPosterBinding binding;

        public ViewHolder(ItemStoryPosterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}