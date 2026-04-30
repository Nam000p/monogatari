package com.monogatari.app.ui.view;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.monogatari.app.data.api.AiApi;
import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.repository.AiRepository;
import com.monogatari.app.databinding.ActivityAiChatBinding;
import com.monogatari.app.ui.adapter.AiChatAdapter;
import com.monogatari.app.ui.viewmodel.AiViewModel;
import com.monogatari.app.ui.viewmodel.AiViewModelFactory;

public class AiChatActivity extends AppCompatActivity {
    private ActivityAiChatBinding binding;
    private AiViewModel viewModel;
    private AiChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AiApi api = ApiClient.getClient(this).create(AiApi.class);
        AiRepository repository = new AiRepository(api);
        viewModel = new ViewModelProvider(this, new AiViewModelFactory(repository)).get(AiViewModel.class);

        setupRecyclerView();
        observeData();

        binding.btnSend.setOnClickListener(v -> {
            String msg = binding.etMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                viewModel.sendMessage(msg);
                binding.etMessage.setText("");
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new AiChatAdapter();
        binding.rvChat.setLayoutManager(new LinearLayoutManager(this));
        binding.rvChat.setAdapter(adapter);
    }

    private void observeData() {
        viewModel.getChatHistory().observe(this, messages -> {
            adapter.setMessages(messages);
            if (!messages.isEmpty()) {
                binding.rvChat.smoothScrollToPosition(messages.size() - 1);
            }
        });

        viewModel.getIsTyping().observe(this, isTyping ->
                binding.pbTyping.setVisibility(isTyping ? View.VISIBLE : View.GONE));
    }
}