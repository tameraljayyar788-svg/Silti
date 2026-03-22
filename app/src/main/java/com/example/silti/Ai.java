package com.example.silti;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silti.databinding.FragmentAiBinding;

import java.util.ArrayList;
import java.util.List;

public class Ai extends Fragment {

    private FragmentAiBinding binding;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatHistory = new ArrayList<>();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    // API Service
    private GeminiApiService apiService;
    private boolean isSending = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // تهيئة API
        apiService = new GeminiApiService();

        setupRecyclerView();
        setupClickListeners();
        setupTextWatcher();

        // رسالة ترحيب
        addWelcomeMessage();
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(chatHistory);
        binding.recyclerChat.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerChat.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        binding.send.setOnClickListener(v -> {
            String message = binding.chat.getText().toString().trim();
            if (!message.isEmpty() && !isSending) {
                sendMessage(message);
            }
        });

        binding.img.setOnClickListener(v -> {
            toggleChat();
        });
    }

    private void setupTextWatcher() {
        binding.chat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    binding.send.setImageResource(R.drawable.send);
                } else {
                    binding.send.setImageResource(R.drawable.send_back);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void toggleChat() {
        if (binding.recyclerChat.getVisibility() == View.VISIBLE) {
            binding.recyclerChat.setVisibility(View.GONE);
            binding.text.setVisibility(View.VISIBLE);
            binding.textHint.setVisibility(View.VISIBLE);
            binding.img.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerChat.setVisibility(View.VISIBLE);
            binding.text.setVisibility(View.GONE);
            binding.textHint.setVisibility(View.GONE);
        }
    }

    private void addWelcomeMessage() {
        String welcome = "مرحباً! أنا المساعد الذكي. كيف يمكنني مساعدتك اليوم؟";
        chatHistory.add(new ChatMessage(welcome, false, System.currentTimeMillis()));
        chatAdapter.notifyItemInserted(chatHistory.size() - 1);
    }

    private void sendMessage(String message) {
        // إضافة رسالة المستخدم
        chatHistory.add(new ChatMessage(message, true, System.currentTimeMillis()));
        chatAdapter.notifyItemInserted(chatHistory.size() - 1);
        binding.recyclerChat.smoothScrollToPosition(chatHistory.size() - 1);

        // مسح حقل الإدخال
        binding.chat.setText("");

        // منع الإرسال المتكرر
        isSending = true;

        // إظهار مؤشر الكتابة
        showTypingIndicator();

        // إرسال إلى API
        apiService.sendMessage(message, new GeminiApiService.GeminiCallback() {
            @Override
            public void onSuccess(String response) {
                isSending = false;
                hideTypingIndicator();

                chatHistory.add(new ChatMessage(response, false, System.currentTimeMillis()));
                chatAdapter.notifyItemInserted(chatHistory.size() - 1);
                binding.recyclerChat.smoothScrollToPosition(chatHistory.size() - 1);
            }

            @Override
            public void onError(String error) {
                isSending = false;
                hideTypingIndicator();

                String errorMessage = "عذراً: " + error;
                chatHistory.add(new ChatMessage(errorMessage, false, System.currentTimeMillis()));
                chatAdapter.notifyItemInserted(chatHistory.size() - 1);
                binding.recyclerChat.smoothScrollToPosition(chatHistory.size() - 1);

                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTypingIndicator() {
        // يمكن إضافة مؤشر كتابة في المستقبل
    }

    private void hideTypingIndicator() {
        // إخفاء مؤشر الكتابة
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainHandler.removeCallbacksAndMessages(null);
        binding = null;
    }
}