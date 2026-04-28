package com.monogatari.app.ui.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.monogatari.app.R;
import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.AuthApi;
import com.monogatari.app.data.api.UserApi;
import com.monogatari.app.data.local.TokenManager;
import com.monogatari.app.data.model.user.UserProfileResponse;
import com.monogatari.app.data.repository.UserRepository;
import com.monogatari.app.databinding.FragmentMeBinding;
import com.monogatari.app.ui.viewmodel.UserViewModel;
import com.monogatari.app.ui.viewmodel.UserViewModelFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeFragment extends Fragment {
    private FragmentMeBinding binding;
    private UserViewModel userViewModel;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "user_cache";
    private static final String KEY_PROFILE = "profile_json";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMeBinding.inflate(inflater, container, false);
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserRepository repository = new UserRepository(ApiClient.getClient(requireContext()).create(UserApi.class));
        userViewModel = new ViewModelProvider(this, new UserViewModelFactory(repository)).get(UserViewModel.class);

        setupUI();
        loadCache();

        observeData();

        userViewModel.fetchProfile();
    }

    private void loadCache() {
        String json = sharedPreferences.getString(KEY_PROFILE, null);
        if (json != null) {
            UserProfileResponse profile = new Gson().fromJson(json, UserProfileResponse.class);
            updateUI(profile);
        }
    }

    private void updateUI(UserProfileResponse profile) {
        if (profile == null) return;
        binding.tvMeUsername.setText(profile.getUsername());
        binding.tvMeEmail.setText(profile.getEmail());
        binding.ivPremiumBadge.setVisibility(profile.isPremium() ? View.VISIBLE : View.GONE);

        String url = profile.getAvatarUrl();
        if (url != null) url = url.replace("http://", "https://");

        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.default_avatar)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(binding.ivMeAvatar);
    }

    private void setupUI() {
        binding.rvReading.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvFollowing.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.swipeRefresh.setOnRefreshListener(() -> userViewModel.fetchProfile());
        binding.btnSettings.setOnClickListener(v -> showSettingsBottomSheet());
    }

    private void observeData() {
        userViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> binding.swipeRefresh.setRefreshing(isLoading));

        userViewModel.getProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                String json = new Gson().toJson(profile);
                sharedPreferences.edit().putString(KEY_PROFILE, json).apply();

                updateUI(profile);
            }
        });

        userViewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Toast.makeText(getContext(), "Sync failed", Toast.LENGTH_SHORT).show();
        });
    }

    private void showSettingsBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());

        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet_settings, null);

        view.findViewById(R.id.btnEditProfile).setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(getActivity(), EditProfileActivity.class));
        });

        view.findViewById(R.id.btnSubscription).setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(getContext(), "Stripe Payment Coming Soon", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btnHelp).setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(getContext(), "Contact: dangxuannam32@gmail.com", Toast.LENGTH_LONG).show();
        });

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            dialog.dismiss();
            new AlertDialog.Builder(requireContext())
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Sign Out", (d, which) -> performLogout())
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void performLogout() {
        sharedPreferences.edit().clear().apply();
        ApiClient.getClient(requireContext()).create(AuthApi.class).logout().enqueue(new Callback<>() {
            @Override public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) { clearSession(); }
            @Override public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) { clearSession(); }
        });
    }

    private void clearSession() {
        TokenManager.getInstance(requireContext()).clearAll();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}