package com.monogatari.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.monogatari.app.data.repository.PaymentRepository;

public class SubscriptionViewModelFactory implements ViewModelProvider.Factory {
    private final PaymentRepository repository;

    public SubscriptionViewModelFactory(PaymentRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SubscriptionViewModel.class)) {
            return (T) new SubscriptionViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}