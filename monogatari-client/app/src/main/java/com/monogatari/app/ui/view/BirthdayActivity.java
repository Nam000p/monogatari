package com.monogatari.app.ui.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.monogatari.app.databinding.ActivityBirthdayInputBinding;
import java.util.Calendar;
import java.util.Locale;

public class BirthdayActivity extends AppCompatActivity {
    private ActivityBirthdayInputBinding binding;

    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBirthdayInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.etBirthday.setOnClickListener(v -> showDatePicker());

        binding.btnSubmit.setOnClickListener(v -> {
            String dob = binding.etBirthday.getText().toString().trim();
            if (dob.isEmpty()) {
                Toast.makeText(this, "Please select your birthday", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateLabel();
        };

        new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel() {
        String format = "yyyy-MM-dd";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format, Locale.US);
        binding.etBirthday.setText(sdf.format(calendar.getTime()));
    }
}