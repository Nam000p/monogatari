package com.monogatari.app.data.api;

import android.content.Context;
import androidx.annotation.NonNull;
import com.monogatari.app.data.local.TokenManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AuthInterceptor implements Interceptor {
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String path = request.url().encodedPath();

        Request.Builder builder = request.newBuilder()
                .header("ngrok-skip-browser-warning", "true");

        if (path.contains("/auth/login") ||
                path.contains("/auth/register") ||
                path.contains("/auth/logout") ||
                path.contains("/auth/refresh")) {

            builder.removeHeader("Authorization");
        } else {
            String token = TokenManager.getInstance(context).getToken();
            if (token != null) {
                builder.header("Authorization", "Bearer " + token);
            }
        }
        return chain.proceed(builder.build());
    }
}