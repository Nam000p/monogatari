package com.monogatari.app.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.monogatari.app.databinding.ItemReaderImageBinding;
import com.monogatari.app.databinding.ItemReaderTextBinding;
import java.util.ArrayList;
import java.util.List;

public class ReaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ReaderItem> items = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<ReaderItem> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
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
        if (viewType == ReaderItem.TYPE_IMAGE) {
            return new ImageViewHolder(ItemReaderImageBinding.inflate(inflater, parent, false));
        }
        return new TextViewHolder(ItemReaderTextBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReaderItem item = items.get(position);
        if (holder instanceof ImageViewHolder) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getContent())
                    .into(((ImageViewHolder) holder).binding.ivReaderImage);
        } else {
            ((TextViewHolder) holder).binding.tvReaderContent.setText(item.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ItemReaderImageBinding binding;
        ImageViewHolder(ItemReaderImageBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {
        ItemReaderTextBinding binding;
        TextViewHolder(ItemReaderTextBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }
}