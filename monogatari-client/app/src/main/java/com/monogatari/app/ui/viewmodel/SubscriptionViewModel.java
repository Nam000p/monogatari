package com.monogatari.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.monogatari.app.data.model.payment.PaymentResponse;
import com.monogatari.app.data.repository.PaymentRepository;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionViewModel extends ViewModel {
    private final PaymentRepository repository;
    private final MutableLiveData<String> checkoutUrl = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public SubscriptionViewModel(PaymentRepository repository) {
        this.repository = repository;
    }

    public LiveData<String> getCheckoutUrl() { return checkoutUrl; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getMessage() { return message; }

    public void createCheckoutSession() {
        isLoading.setValue(true);
        repository.createCheckout().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    checkoutUrl.setValue(response.body().get("url"));
                } else {
                    error.setValue("Failed to create payment session");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    public void cancelSubscription() {
        isLoading.setValue(true);
        repository.cancelSubscription().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PaymentResponse> call, @NonNull Response<PaymentResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    message.setValue(response.body().getMessage());
                } else {
                    error.setValue("Failed to cancel subscription");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaymentResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }
}