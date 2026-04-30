package com.monogatari.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.monogatari.app.data.model.ai.AiResponse;
import com.monogatari.app.data.model.ai.ChatMessage;
import com.monogatari.app.data.repository.AiRepository;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiViewModel extends ViewModel {
    private final AiRepository repository;
    private final MutableLiveData<List<ChatMessage>> chatHistory = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isTyping = new MutableLiveData<>(false);

    public AiViewModel(AiRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<ChatMessage>> getChatHistory() { return chatHistory; }
    public LiveData<Boolean> getIsTyping() { return isTyping; }

    public void sendMessage(String message) {
        List<ChatMessage> currentMessages = chatHistory.getValue();
        if (currentMessages == null) currentMessages = new ArrayList<>();

        currentMessages.add(new ChatMessage(message, ChatMessage.TYPE_USER));
        chatHistory.setValue(currentMessages);

        isTyping.setValue(true);
        repository.chatWithAi(message).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AiResponse> call, @NonNull Response<AiResponse> response) {
                isTyping.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    addAiMessage(response.body().getReply());
                } else {
                    addAiMessage("Error: Could not get a response from AI.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AiResponse> call, @NonNull Throwable t) {
                isTyping.setValue(false);
                addAiMessage("Network Error: " + t.getMessage());
            }
        });
    }

    private void addAiMessage(String reply) {
        List<ChatMessage> currentMessages = chatHistory.getValue();
        if (currentMessages != null) {
            currentMessages.add(new ChatMessage(reply, ChatMessage.TYPE_AI));
            chatHistory.setValue(currentMessages);
        }
    }
}