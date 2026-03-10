package com.example.silti;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.silti.databinding.ActivitySignupBinding;

public class signup extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModel();
        setupClickListeners();
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void setupClickListeners() {
        binding.signup.setOnClickListener(v -> performSignup());

        binding.login.setOnClickListener(v -> {
            startActivity(new Intent(signup.this, login.class));
            finish();
        });
    }

    private void performSignup() {
        String username = binding.username.getText().toString().trim();
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        userViewModel.isEmailExists(email, exists -> {
            if (exists) {
                runOnUiThread(() ->
                        Toast.makeText(signup.this, "البريد الإلكتروني مستخدم مسبقاً", Toast.LENGTH_LONG).show());
            } else {
                User newUser = new User(
                        null,
                        username,
                        email,
                        password,
                        null,
                        null,
                        null,
                        null,
                        false,
                        System.currentTimeMillis()
                );

                userViewModel.insert(newUser);

                runOnUiThread(() -> {
                    Toast.makeText(signup.this, "تم إنشاء الحساب بنجاح", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(signup.this, login.class));
                    finish();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}