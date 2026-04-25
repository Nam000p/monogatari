package com.monogatari.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.monogatari.app.data.model.chapter.ChapterResponse;
import com.monogatari.app.databinding.ItemChapterBinding;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
    private List<ChapterResponse> chapterList;
    private OnChapterClickListener listener;

    public interface OnChapterClickListener {
        void onChapterClick(ChapterResponse chapter);
    }

    public ChapterAdapter(List<ChapterResponse> chapterList, OnChapterClickListener listener) {
        this.chapterList = chapterList;
        this.listener = listener;
    }

    public void setChapters(List<ChapterResponse> chapters) {
        this.chapterList = chapters;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChapterBinding binding = ItemChapterBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ChapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        holder.bind(chapterList.get(position));
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    class ChapterViewHolder extends RecyclerView.ViewHolder {
        private final ItemChapterBinding binding;

        public ChapterViewHolder(ItemChapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(v -> listener.onChapterClick(chapterList.get(getAdapterPosition())));
        }

        public void bind(ChapterResponse chapter) {
            binding.tvChapterNumber.setText("Chapter " + chapter.getChapterNumber());
            binding.tvChapterTitle.setText(chapter.getTitle());

            if (chapter.getIsPremium() != null && chapter.getIsPremium()) {
                binding.ivPremiumIcon.setVisibility(View.VISIBLE);
            } else {
                binding.ivPremiumIcon.setVisibility(View.GONE);
            }
        }
    }
}