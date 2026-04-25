package com.monogatari.app.data.api;

import com.monogatari.app.data.model.ai.AiRequest;
import com.monogatari.app.data.model.ai.AiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AiApi {
    @POST("ai/chat")
    Call<AiResponse> chatWithAi(@Body AiRequest request);
}