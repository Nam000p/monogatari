package com.monogatari.app.data.repository;

import com.monogatari.app.data.api.AiApi;
import com.monogatari.app.data.model.ai.AiRequest;
import com.monogatari.app.data.model.ai.AiResponse;

import retrofit2.Call;

public class AiRepository {
    private final AiApi aiApi;

    public AiRepository(AiApi aiApi) {
        this.aiApi = aiApi;
    }

    public Call<AiResponse> chatWithAi(String message) {
        AiRequest request = new AiRequest(message);
        return aiApi.chatWithAi(request);
    }
}