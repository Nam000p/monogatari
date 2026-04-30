package com.monogatari.app.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.monogatari.app.data.model.story.StoryResponse;
import com.monogatari.app.databinding.ItemBannerBinding;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<StoryResponse> bannerStories = new ArrayList<>();
    private final OnBannerClickListener listener;

    public interface OnBannerClickListener {
        void onBannerClick(StoryResponse story);
    }

    public BannerAdapter(OnBannerClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setBannerStories(List<StoryResponse> stories) {
        this.bannerStories = stories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBannerBinding binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BannerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.bind(bannerStories.get(position));
    }

    @Override
    public int getItemCount() {
        return bannerStories.size();
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        private final ItemBannerBinding binding;

        public BannerViewHolder(ItemBannerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(StoryResponse story) {
            Glide.with(binding.getRoot().getContext())
                    .load(story.getCoverUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivBannerCover);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBannerClick(story);
                }
            });
        }
    }
}