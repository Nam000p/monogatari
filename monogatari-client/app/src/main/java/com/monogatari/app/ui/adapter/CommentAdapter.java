package com.monogatari.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monogatari.app.data.model.comment.CommentResponse;
import com.monogatari.app.databinding.ItemCommentBinding;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<CommentResponse> commentList;

    public CommentAdapter(List<CommentResponse> commentList) {
        this.commentList = commentList;
    }

    public void setComments(List<CommentResponse> comments) {
        this.commentList = comments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(commentList.get(position));
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        private final ItemCommentBinding binding;

        public CommentViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CommentResponse comment) {
            binding.tvUsername.setText(comment.getUsername());
            binding.tvContent.setText(comment.getContent());
            if (comment.getCreatedAt() != null) {
                binding.tvTimestamp.setText(comment.getCreatedAt().toString());
            }
        }
    }
}