package com.monogatari.app.data.api;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.monogatari.app.data.local.TokenManager;
import com.monogatari.app.data.model.auth.AuthResponse;
import com.monogatari.app.data.repository.AuthRepository;
import java.io.IOException;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {
    private final Context context;

    public TokenAuthenticator(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        if (responseCount(response) >= 2) {
            return null;
        }

        TokenManager tokenManager = TokenManager.getInstance(context);

        AuthApi authApi = ApiClient.getClient(context).create(AuthApi.class);
        AuthRepository authRepository = new AuthRepository(authApi);

        retrofit2.Response<AuthResponse> refreshResponse = authRepository.refreshToken().execute();

        if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
            AuthResponse authResponse = refreshResponse.body();
            String newToken = authResponse.getToken();
            String newRefreshToken = authResponse.getRefreshToken();

            tokenManager.saveTokens(newToken, newRefreshToken);

            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + newToken)
                    .build();
        } else {
            tokenManager.clearAll();
            return null;
        }
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}