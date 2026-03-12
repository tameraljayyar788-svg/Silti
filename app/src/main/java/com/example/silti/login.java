package com.example.silti;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.silti.databinding.ActivityAdmenBinding;
import com.example.silti.databinding.ActivityLoginBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class login extends AppCompatActivity {


    private ActivityLoginBinding binding;
    private UserViewModel userViewModel;
    private SharedPreferences sharedPreferences;

    // Admin default credentials
    private static final String ADMIN_EMAIL = "admin@mycart.com";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModel();
        checkSavedLogin();
        setupClickListeners();

        // Optional: Auto-fill admin credentials for testing
        // binding.email.setText(ADMIN_EMAIL);
        // binding.password.setText(ADMIN_PASSWORD);
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        sharedPreferences = getSharedPreferences("MyCartPrefs", MODE_PRIVATE);

        // Ensure admin account exists in database
        checkAndCreateAdminAccount();
    }

    private void checkAndCreateAdminAccount() {
        userViewModel.isEmailExists(ADMIN_EMAIL, exists -> {
            if (!exists) {
                // Create admin account if not exists
                User adminUser = new User(
                        null,
                        "Admin",
                        ADMIN_EMAIL,
                        ADMIN_PASSWORD,
                        "0123456789",
                        null,
                        "Admin Address",
                        "Admin City",
                        true, // isAdmin = true
                        System.currentTimeMillis()
                );

                userViewModel.insert(adminUser);
            }
        });
    }

    private void checkSavedLogin() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        long userId = sharedPreferences.getLong("userId", -1);

        if (isLoggedIn && userId != -1) {
            userViewModel.getUserById(userId).observe(this, user -> {
                if (user != null) {
                    navigateBasedOnRole(user);
                }
            });
        }
    }

    private void setupClickListeners() {
        binding.login.setOnClickListener(v -> performLogin());

        binding.signup.setOnClickListener(v ->
                startActivity(new Intent(login.this, signup.class)));

        binding.forget.setOnClickListener(v ->
                Toast.makeText(this, "سيتم إرسال رابط استعادة كلمة المرور إلى بريدك الإلكتروني", Toast.LENGTH_LONG).show());
    }

    private void performLogin() {
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress (optional)
        binding.login.setEnabled(false);
        binding.login.setText("جاري تسجيل الدخول...");

        // Check admin login first
        if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
            // Admin login
            userViewModel.getUserByEmail(email, new UserRepository.UserCallback() {
                @Override
                public void onResult(User user) {
                    runOnUiThread(() -> {
                        binding.login.setEnabled(true);
                        binding.login.setText("تسجيل الدخول");

                        if (user != null) {
                            saveUserSession(user);
                            Toast.makeText(login.this, "مرحباً بك يا أدمن", Toast.LENGTH_SHORT).show();
                            navigateToAdmin();
                        } else {
                            Toast.makeText(login.this, "خطأ في بيانات الأدمن", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            return;
        }

        // Regular user login
        userViewModel.loginUser(email, password, new UserRepository.LoginCallback() {
            @Override
            public void onResult(User user) {
                runOnUiThread(() -> {
                    binding.login.setEnabled(true);
                    binding.login.setText("تسجيل الدخول");

                    if (user != null) {
                        saveUserSession(user);
                        Toast.makeText(login.this, "مرحباً " + user.getUsername(), Toast.LENGTH_SHORT).show();
                        navigateBasedOnRole(user);
                    } else {
                        Toast.makeText(login.this, "البريد الإلكتروني أو كلمة المرور غير صحيحة", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void saveUserSession(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putLong("userId", user.getId());
        editor.putBoolean("isAdmin", user.isAdmin());
        editor.putString("userEmail", user.getEmail());
        editor.putString("userName", user.getUsername());
        editor.apply();

        userViewModel.setCurrentUser(user.getId());
    }

    private void navigateBasedOnRole(User user) {
        if (user.isAdmin()) {
            navigateToAdmin();
        } else {
            navigateToMain();
        }
    }

    private void navigateToAdmin() {
        Intent intent = new Intent(login.this, Admen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToMain() {
        Intent intent = new Intent(login.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}