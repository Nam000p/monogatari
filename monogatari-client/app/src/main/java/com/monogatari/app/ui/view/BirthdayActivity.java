package com.monogatari.app.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.UserApi;
import com.monogatari.app.data.model.user.UserProfileResponse;
import com.monogatari.app.data.model.user.UserUpdateProfileRequest;
import com.monogatari.app.data.repository.UserRepository;
import com.monogatari.app.databinding.ActivityBirthdayInputBinding;

import java.time.LocalDate;
import java.time.ZoneOffset;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BirthdayActivity extends AppCompatActivity {
    private ActivityBirthdayInputBinding binding;
    private UserRepository userRepository;
    private String selectedBirthDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBirthdayInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = new UserRepository(ApiClient.getClient(this).create(UserApi.class));

        binding.etBirthday.setOnClickListener(v -> showDatePicker());

        binding.btnSubmit.setOnClickListener(v -> {
            if (selectedBirthDate == null) {
                Toast.makeText(this, "Please select your birthday", Toast.LENGTH_SHORT).show();
                return;
            }
            updateBirthday();
        });

        binding.btnSkip.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void showDatePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Birth Date");

        if (selectedBirthDate != null && !selectedBirthDate.isEmpty()) {
            try {
                LocalDate tempDate = LocalDate.parse(selectedBirthDate);
                long selection = tempDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
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

                binding.etBirthday.setText(selectedBirthDate);
            }
        });

        picker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    }

    private void updateBirthday() {
        binding.btnSubmit.setEnabled(false);

        UserUpdateProfileRequest request = new UserUpdateProfileRequest();
        request.setBirthDate(selectedBirthDate);

        userRepository.updateProfile(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                binding.btnSubmit.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(BirthdayActivity.this, "Birthday updated!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BirthdayActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(BirthdayActivity.this, "Update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {
                binding.btnSubmit.setEnabled(true);
                Toast.makeText(BirthdayActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}