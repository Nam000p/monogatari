package com.monogatari.app.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monogatari.app.data.model.chapter.ChapterResponse;
import com.monogatari.app.databinding.ItemChapterBinding;

import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private List<ChapterResponse> chapters = new ArrayList<>();
    private final OnChapterClickListener listener;

    public interface OnChapterClickListener {
        void onChapterClick(ChapterResponse chapter);
    }

    public ChapterAdapter(OnChapterClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setChapters(List<ChapterResponse> newChapters) {
        this.chapters = newChapters;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChapterBinding binding = ItemChapterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        holder.bind(chapters.get(position));
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    class ChapterViewHolder extends RecyclerView.ViewHolder {
        private final ItemChapterBinding binding;

        public ChapterViewHolder(ItemChapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ChapterResponse chapter) {
            String chapterNumStr = chapter.getChapterNumber() != null ?
                    String.valueOf(chapter.getChapterNumber()) : "?";

            if (chapterNumStr.endsWith(".0")) {
                chapterNumStr = chapterNumStr.replace(".0", "");
            }
            binding.tvChapterNumber.setText(chapterNumStr);

            binding.tvChapterTitle.setText(chapter.getTitle() != null ? chapter.getTitle() : "Chapter " + chapterNumStr);

            binding.tvChapterDate.setVisibility(View.GONE);

            if (chapter.getIsPremium() != null && chapter.getIsPremium()) {
                binding.ivPremiumLock.setVisibility(View.VISIBLE);
            } else {
                binding.ivPremiumLock.setVisibility(View.GONE);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChapterClick(chapter);
                }
            });
        }
    }
}