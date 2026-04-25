package com.monogatari.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.monogatari.app.R;
import java.util.List;

public class MangaAdapter extends RecyclerView.Adapter<MangaAdapter.MangaViewHolder> {
    private List<String> imageUrls;

    public MangaAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public MangaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo một cái ImageView đơn giản hoặc dùng layout item_manga_image
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setAdjustViewBounds(true); // Quan trọng để ảnh không bị méo
        return new MangaViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull MangaViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(imageUrls.get(position))
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into((ImageView) holder.itemView);
    }

    @Override
    public int getItemCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    static class MangaViewHolder extends RecyclerView.ViewHolder {
        public MangaViewHolder(@NonNull ImageView itemView) {
            super(itemView);
        }
    }
}