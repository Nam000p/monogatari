package com.monogatari.app.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.monogatari.app.data.model.progress.ReadingProgressResponse;
import com.monogatari.app.databinding.ItemStoryPosterBinding;
import java.util.ArrayList;
import java.util.List;

public class ReadingProgressAdapter extends RecyclerView.Adapter<ReadingProgressAdapter.ProgressViewHolder> {
    private final List<ReadingProgressResponse> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Long storyId);
    }

    public ReadingProgressAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<ReadingProgressResponse> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStoryPosterBinding binding = ItemStoryPosterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProgressViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        ReadingProgressResponse item = items.get(position);
        Glide.with(holder.binding.getRoot().getContext())
                .load(item.getCoverUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.binding.ivCover);

        holder.binding.getRoot().setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item.getStoryId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        final ItemStoryPosterBinding binding;
        ProgressViewHolder(ItemStoryPosterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}