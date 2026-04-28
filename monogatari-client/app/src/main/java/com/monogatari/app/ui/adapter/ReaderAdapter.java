package com.monogatari.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.monogatari.app.R;
import java.util.ArrayList;
import java.util.List;

public class ReaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ReaderItem> items = new ArrayList<>();
    private final OnPageClickListener clickListener;

    public interface OnPageClickListener {
        void onPageClick();
    }

    public ReaderAdapter(OnPageClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void submitList(List<ReaderItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ReaderItem.TYPE_TEXT) {
            View view = inflater.inflate(R.layout.item_reader_text, parent, false);
            return new TextViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_reader_image, parent, false);
            return new ImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReaderItem item = items.get(position);

        if (holder instanceof TextViewHolder) {
            ((TextViewHolder) holder).bind(item.getContent());
        } else if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).bind(item.getContent());
        }

        holder.itemView.setOnClickListener(v -> clickListener.onPageClick());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvContent;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvReaderContent);
        }

        public void bind(String text) {
            tvContent.setText(text);
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivImage;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivReaderImage);
        }

        public void bind(String imageUrl) {
            // Glide.with(itemView.getContext()).load(imageUrl).into(ivImage);
        }
    }
}