package com.monogatari.app.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.monogatari.app.data.model.comment.CommentResponse;
import com.monogatari.app.databinding.ItemCommentBinding;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final List<CommentResponse> commentList = new ArrayList<>();
    private Long currentUserId;
    private boolean isAdmin;
    private OnCommentDeleteListener deleteListener;

    public interface OnCommentDeleteListener {
        void onDelete(Long commentId);
    }

    public void setUserInfo(Long userId, boolean isAdmin) {
        this.currentUserId = userId;
        this.isAdmin = isAdmin;
    }

    public void setOnDeleteListener(OnCommentDeleteListener listener) {
        this.deleteListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setComments(List<CommentResponse> comments) {
        this.commentList.clear();
        this.commentList.addAll(comments);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentResponse comment = commentList.get(position);
        holder.binding.tvUsername.setText(comment.getUsername());
        holder.binding.tvCommentContent.setText(comment.getContent());

        boolean canDelete = isAdmin || (currentUserId != null && currentUserId.equals(comment.getUserId()));
        holder.binding.btnDeleteComment.setVisibility(canDelete ? android.view.View.VISIBLE : android.view.View.GONE);

        holder.binding.btnDeleteComment.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(comment.getId());
        });

        Glide.with(holder.itemView.getContext()).load(comment.getAvatarUrl()).into(holder.binding.ivUserAvatar);
    }

    @Override public int getItemCount() { return commentList.size(); }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CommentViewHolder(binding);
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        final ItemCommentBinding binding;
        CommentViewHolder(ItemCommentBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }
}