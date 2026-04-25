package com.monogatari.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monogatari.app.data.model.genre.GenreResponse;
import com.monogatari.app.databinding.ItemGenreBinding;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {
    private List<GenreResponse> genreList;
    private OnGenreClickListener listener;

    public interface OnGenreClickListener {
        void onGenreClick(GenreResponse genre);
    }

    public GenreAdapter(List<GenreResponse> genreList, OnGenreClickListener listener) {
        this.genreList = genreList;
        this.listener = listener;
    }

    public void setGenres(List<GenreResponse> genres) {
        this.genreList = genres;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGenreBinding binding = ItemGenreBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new GenreViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        holder.bind(genreList.get(position));
    }

    @Override
    public int getItemCount() {
        return genreList != null ? genreList.size() : 0;
    }

    class GenreViewHolder extends RecyclerView.ViewHolder {
        private final ItemGenreBinding binding;

        public GenreViewHolder(ItemGenreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onGenreClick(genreList.get(position));
                }
            });
        }

        public void bind(GenreResponse genre) {
            binding.tvGenreName.setText(genre.getName());
        }
    }
}