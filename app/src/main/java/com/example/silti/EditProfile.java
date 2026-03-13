package com.example.silti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.silti.databinding.ActivityEditProfileBinding;

public class EditProfile extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private UserViewModel userViewModel;
    private long currentUserId;
    private User currentUser;
    private String newImagePath = "";

    // Image picker launcher
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        newImagePath = selectedImageUri.toString();
                        binding.image.setImageURI(selectedImageUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModel();
        getCurrentUser();
        loadUserData();
        setupClickListeners();
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void getCurrentUser() {
        SharedPreferences prefs = getSharedPreferences("MyCartPrefs", MODE_PRIVATE);
        currentUserId = prefs.getLong("userId", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "الرجاء تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadUserData() {
        userViewModel.getUserById(currentUserId).observe(this, user -> {
            if (user != null) {
                currentUser = user;

                // عرض البيانات الحالية في الحقول
                binding.editUsername.setText(user.getUsername());
                binding.editEmail.setText(user.getEmail());
                binding.editPassword.setText(user.getPassword());

                // تحميل الصورة الشخصية
                if (user.getImageProfile() != null && !user.getImageProfile().isEmpty()) {
                    Glide.with(this)
                            .load(user.getImageProfile())
                            .placeholder(R.drawable.profile_defult)
                            .error(R.drawable.profile_defult)
                            .circleCrop()
                            .into(binding.image);
                } else {
                    binding.image.setImageResource(R.drawable.profile_defult);
                }
            }
        });
    }

    private void setupClickListeners() {
        // زر العودة
        binding.back.setOnClickListener(v -> finish());

        // اختيار صورة جديدة
        binding.image.setOnClickListener(v -> openImagePicker());

        // حفظ التعديلات
        binding.bEdit.setOnClickListener(v -> saveChanges());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void saveChanges() {
        String newUsername = binding.editUsername.getText().toString().trim();
        String newEmail = binding.editEmail.getText().toString().trim();
        String newPassword = binding.editPassword.getText().toString().trim();

        // التحقق من الحقول
        if (TextUtils.isEmpty(newUsername)) {
            binding.editUsername.setError("اسم المستخدم مطلوب");
            return;
        }

        if (TextUtils.isEmpty(newEmail)) {
            binding.editEmail.setError("البريد الإلكتروني مطلوب");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            binding.editEmail.setError("البريد الإلكتروني غير صالح");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            binding.editPassword.setError("كلمة المرور مطلوبة");
            return;
        }

        if (newPassword.length() < 6) {
            binding.editPassword.setError("كلمة المرور يجب أن تكون 6 أحرف على الأقل");
            return;
        }

        // التحقق من عدم استخدام البريد الإلكتروني من قبل مستخدم آخر
        if (!newEmail.equals(currentUser.getEmail())) {
            userViewModel.isEmailExists(newEmail, exists -> {
                if (exists) {
                    runOnUiThread(() -> {
                        binding.editEmail.setError("البريد الإلكتروني مستخدم مسبقاً");
                    });
                } else {
                    updateUserData(newUsername, newEmail, newPassword);
                }
            });
        } else {
            updateUserData(newUsername, newEmail, newPassword);
        }
    }

    private void updateUserData(String username, String email, String password) {
        // تحديث بيانات المستخدم
        currentUser.setUsername(username);
        currentUser.setEmail(email);
        currentUser.setPassword(password);

        if (!newImagePath.isEmpty()) {
            currentUser.setImageProfile(newImagePath);
        }

        userViewModel.update(currentUser);

        Toast.makeText(this, "تم حفظ التعديلات بنجاح", Toast.LENGTH_SHORT).show();

        // العودة إلى الشاشة السابقة
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}