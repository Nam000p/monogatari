package com.monogatari.app.data.api;

import android.content.Context;
import com.monogatari.app.data.local.TokenManager;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = TokenManager.getInstance(context).getToken();
        Request.Builder builder = chain.request().newBuilder();

        // If token exists, add it to Authorization header
        if (token != null) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        return chain.proceed(builder.build());
    }
}