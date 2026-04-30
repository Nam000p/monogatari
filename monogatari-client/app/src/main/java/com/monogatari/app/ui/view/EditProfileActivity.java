package com.monogatari.app.ui.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.monogatari.app.R;
import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.UserApi;
import com.monogatari.app.data.local.TokenManager;
import com.monogatari.app.data.model.user.UserProfileResponse;
import com.monogatari.app.data.model.user.UserUpdateProfileRequest;
import com.monogatari.app.data.repository.UserRepository;
import com.monogatari.app.databinding.ActivityEditProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private UserRepository userRepository;
    private String selectedBirthDate;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) uploadAvatar(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = new UserRepository(ApiClient.getClient(this).create(UserApi.class));

        initViews();
        loadCurrentProfile();
    }

    private void initViews() {
        binding.ivEditAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        binding.etBirthDate.setOnClickListener(v -> showDatePicker());
        binding.btnSaveProfile.setOnClickListener(v -> updateProfile());
        binding.btnDeleteAccount.setOnClickListener(v -> confirmDeleteAccount());
    }

    private void loadCurrentProfile() {
        userRepository.getMyProfile().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profile = response.body();
                    binding.etUsername.setText(profile.getUsername());
                    if (profile.getBirthDate() != null) {
                        selectedBirthDate = profile.getBirthDate();
                        binding.etBirthDate.setText(selectedBirthDate);
                    }
                    Glide.with(EditProfileActivity.this)
                            .load(profile.getAvatarUrl())
                            .circleCrop()
                            .into(binding.ivEditAvatar);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadAvatar(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Toast.makeText(this, "Cannot read image", Toast.LENGTH_SHORT).show();
                return;
            }

            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            byte[] bytes = byteBuffer.toByteArray();
            inputStream.close();

            String mimeType = getContentResolver().getType(uri);
            if (mimeType == null) mimeType = "image/jpeg";

            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), bytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "avatar.jpg", requestFile);

            binding.ivEditAvatar.setAlpha(0.5f);

            userRepository.updateAvatar(body).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                    binding.ivEditAvatar.setAlpha(1.0f);
                    if (response.isSuccessful()) {
                        Glide.with(EditProfileActivity.this).load(uri).circleCrop().into(binding.ivEditAvatar);
                        Toast.makeText(EditProfileActivity.this, "Avatar updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {
                    binding.ivEditAvatar.setAlpha(1.0f);
                    Toast.makeText(EditProfileActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile() {
        String username = binding.etUsername.getText().toString().trim();
        if (username.isEmpty()) return;

        UserUpdateProfileRequest request = new UserUpdateProfileRequest();
        request.setUsername(username);
        request.setBirthDate(selectedBirthDate);

        userRepository.updateProfile(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Birth Date");

        try {
            builder.setTheme(R.style.MaterialCalendarTheme);
        } catch (Exception ignored) {}

        if (selectedBirthDate != null && !selectedBirthDate.isEmpty()) {
            try {
                LocalDate tempDate = LocalDate.parse(selectedBirthDate);
                long selection = tempDate.atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli();
                builder.setSelection(selection);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        final MaterialDatePicker<Long> picker = builder.build();

        picker.addOnPositiveButtonClickListener((Long selection) -> {
            if (selection != null) {
                java.util.Calendar calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(selection);

                selectedBirthDate = String.format(java.util.Locale.US, "%04d-%02d-%02d",
                        calendar.get(java.util.Calendar.YEAR),
                        calendar.get(java.util.Calendar.MONTH) + 1,
                        calendar.get(java.util.Calendar.DAY_OF_MONTH)
                );

                binding.etBirthDate.setText(selectedBirthDate);
            }
        });

        picker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        userRepository.deleteMyAccount().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                TokenManager.getInstance(EditProfileActivity.this).clearAll();
                Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}