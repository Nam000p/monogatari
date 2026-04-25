package com.monogatari.app.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.AiApi;
import com.monogatari.app.data.api.AuthorApi;
import com.monogatari.app.data.api.ChapterApi;
import com.monogatari.app.data.api.CommentApi;
import com.monogatari.app.data.api.GenreApi;
import com.monogatari.app.data.api.PaymentApi;
import com.monogatari.app.data.api.RatingApi;
import com.monogatari.app.data.api.ReadingProgressApi;
import com.monogatari.app.data.api.StoryApi;
import com.monogatari.app.data.repository.AiRepository;
import com.monogatari.app.data.repository.AuthorRepository;
import com.monogatari.app.data.repository.ChapterRepository;
import com.monogatari.app.data.repository.CommentRepository;
import com.monogatari.app.data.repository.GenreRepository;
import com.monogatari.app.data.repository.PaymentRepository;
import com.monogatari.app.data.repository.RatingRepository;
import com.monogatari.app.data.repository.ReadingProgressRepository;
import com.monogatari.app.data.repository.StoryRepository;
import com.monogatari.app.databinding.ActivityAiChatBinding;
import com.monogatari.app.ui.viewmodel.StoryViewModel;
import com.monogatari.app.ui.viewmodel.StoryViewModelFactory;

public class AiChatActivity extends AppCompatActivity {
    private ActivityAiChatBinding binding;
    private StoryViewModel storyViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FIX LỖI VĂNG: Đảm bảo binding được khởi tạo trước khi dùng
        binding = ActivityAiChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarAi);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViewModel();
        setupChatAction();
        setupObservers();
    }

    private void setupChatAction() {
        binding.btnSend.setOnClickListener(v -> {
            String message = binding.etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                // Hiển thị trạng thái đang gửi
                binding.btnSend.setEnabled(false);
                storyViewModel.chatWithAi(message);
                binding.etMessage.setText("");
            }
        });
    }

    private void setupObservers() {
        storyViewModel.getAiResponse().observe(this, response -> {
            binding.btnSend.setEnabled(true);
            if (response != null) {
                // Thêm vào list chat (ông cần cái ChatAdapter ở đây)
                Toast.makeText(this, "The Archivist: " + response.getReply(), Toast.LENGTH_LONG).show();
            }
        });

        storyViewModel.getErrorMessage().observe(this, error -> {
            binding.btnSend.setEnabled(true);
            if (error != null) {
                // LOGIC GO TO PREMIUM: Nếu Backend trả về 403 (Access Denied)
                if (error.contains("403") || error.contains("premium")) {
                    showPremiumDialog();
                } else {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showPremiumDialog() {
        new AlertDialog.Builder(this)
                .setTitle("EXCLUSIVE ARCHIVES")
                .setMessage("My apologies, sir. 'The Archivist' only consults with our premium members. Would you like to view our subscription plans?")
                .setPositiveButton("GO TO PREMIUM", (dialog, which) -> {
                    Intent intent = new Intent(this, PaymentActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("MAYBE LATER", null)
                .show();
    }

    private void initViewModel() {
        StoryApi storyApi = ApiClient.getClient(this).create(StoryApi.class);
        ChapterApi chapterApi = ApiClient.getClient(this).create(ChapterApi.class);
        CommentApi commentApi = ApiClient.getClient(this).create(CommentApi.class);
        RatingApi ratingApi = ApiClient.getClient(this).create(RatingApi.class);
        ReadingProgressApi progressApi = ApiClient.getClient(this).create(ReadingProgressApi.class);
        GenreApi genreApi = ApiClient.getClient(this).create(GenreApi.class);
        AuthorApi authorApi = ApiClient.getClient(this).create(AuthorApi.class);
        AiApi aiApi = ApiClient.getClient(this).create(AiApi.class);
        PaymentApi paymentApi = ApiClient.getClient(this).create(PaymentApi.class);

        StoryRepository storyRepo = new StoryRepository(storyApi);
        ChapterRepository chapterRepo = new ChapterRepository(chapterApi);
        CommentRepository commentRepo = new CommentRepository(commentApi);
        RatingRepository ratingRepo = new RatingRepository(ratingApi);
        ReadingProgressRepository progressRepo = new ReadingProgressRepository(progressApi);
        GenreRepository genreRepo = new GenreRepository(genreApi);
        AuthorRepository authorRepo = new AuthorRepository(authorApi);
        AiRepository aiRepo = new AiRepository(aiApi);
        PaymentRepository paymentRepo = new PaymentRepository(paymentApi);

        StoryViewModelFactory factory = new StoryViewModelFactory(
                storyRepo, chapterRepo, commentRepo, ratingRepo, progressRepo, genreRepo, authorRepo, aiRepo, paymentRepo);
        storyViewModel = new ViewModelProvider(this, factory).get(StoryViewModel.class);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}