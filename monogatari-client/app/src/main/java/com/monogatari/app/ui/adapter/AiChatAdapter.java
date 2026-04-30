package com.monogatari.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.monogatari.app.R;
import com.monogatari.app.data.model.ai.ChatMessage;
import java.util.ArrayList;
import java.util.List;

public class AiChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> messages = new ArrayList<>();

    public void setMessages(List<ChatMessage> newMessages) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ChatMessageDiffCallback(this.messages, newMessages));
        this.messages.clear();
        this.messages.addAll(newMessages);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == ChatMessage.TYPE_USER) ? R.layout.item_chat_user : R.layout.item_chat_ai;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ChatViewHolder) holder).bind(messages.get(position));
    }

    @Override
    public int getItemCount() { return messages.size(); }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMessage;

        ChatViewHolder(View v) {
            super(v);
            tvMessage = v.findViewById(R.id.tvChatMessage);
        }

        void bind(ChatMessage message) {
            tvMessage.setText(message.getContent());
        }
    }

    private static class ChatMessageDiffCallback extends DiffUtil.Callback {
        private final List<ChatMessage> oldList;
        private final List<ChatMessage> newList;

        ChatMessageDiffCallback(List<ChatMessage> oldList, List<ChatMessage> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() { return oldList.size(); }

        @Override
        public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldPos, int newPos) {
            return oldList.get(oldPos).getContent().equals(newList.get(newPos).getContent())
                    && oldList.get(oldPos).getType() == newList.get(newPos).getType();
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {
            return oldList.get(oldPos).equals(newList.get(newPos));
        }
    }
}