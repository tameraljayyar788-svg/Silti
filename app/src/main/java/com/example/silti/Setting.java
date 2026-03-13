package com.example.silti;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.silti.databinding.ActivitySettingBinding;

public class Setting extends AppCompatActivity {

    private ActivitySettingBinding binding;
    private UserViewModel userViewModel;
    private long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModel();
        getCurrentUser();
        setupClickListeners();
        loadThemePreference();
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void getCurrentUser() {
        SharedPreferences prefs = getSharedPreferences("MyCartPrefs", MODE_PRIVATE);
        currentUserId = prefs.getLong("userId", -1);
    }

    private void setupClickListeners() {
        // زر العودة
        binding.back.setOnClickListener(v -> finish());

        // تعديل البيانات - الانتقال إلى شاشة تعديل الملف الشخصي
        binding.editData.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfile.class);
            startActivity(intent);
        });

        // تغيير السمة (الوضع الليلي/النهاري)
        binding.mood.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveThemePreference(isChecked);
            applyTheme(isChecked);
        });

        // حذف الحساب
        binding.delete.setOnClickListener(v -> showDeleteAccountDialog());

        // سياسة الخصوصية
        binding.privity.setOnClickListener(v -> {
            // فتح صفحة سياسة الخصوصية (يمكن أن تكون WebView أو رابط)
            showPrivacyPolicy();
        });

        // تواصل معنا
        binding.keepOnTouch.setOnClickListener(v -> {
            contactUs();
        });

        // حول التطبيق
        binding.aboutApp.setOnClickListener(v -> {
            showAboutDialog();
        });
    }

    private void loadThemePreference() {
        SharedPreferences prefs = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        binding.mood.setChecked(isDarkMode);
    }

    private void saveThemePreference(boolean isDarkMode) {
        SharedPreferences prefs = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
        prefs.edit().putBoolean("dark_mode", isDarkMode).apply();
    }

    private void applyTheme(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        // إعادة تشغيل النشاط لتطبيق التغيير
        recreate();
    }

    private void showDeleteAccountDialog() {
        if (currentUserId == -1) {
            Toast.makeText(this, "الرجاء تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("حذف الحساب")
                .setMessage("هل أنت متأكد من حذف حسابك؟ لا يمكن التراجع عن هذا الإجراء.")
                .setPositiveButton("نعم، احذف", (dialog, which) -> {
                    deleteAccount();
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void deleteAccount() {
        // حذف المستخدم من قاعدة البيانات
        userViewModel.deleteById(currentUserId);

        // مسح الجلسة
        SharedPreferences prefs = getSharedPreferences("MyCartPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Toast.makeText(this, "تم حذف الحساب بنجاح", Toast.LENGTH_LONG).show();

        // العودة إلى شاشة تسجيل الدخول
        navigateToLogin();
    }

    private void showPrivacyPolicy() {
        // يمكن عرض سياسة الخصوصية في WebView أو فتح رابط
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("سياسة الخصوصية")
                .setMessage("هذه هي سياسة الخصوصية للتطبيق...\n\n" +
                        "نحن نحترم خصوصيتك ونلتزم بحماية بياناتك الشخصية.")
                .setPositiveButton("موافق", null)
                .show();
    }

    private void contactUs() {
        // فتح البريد الإلكتروني للتواصل
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@mycart.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "استفسار حول تطبيق سلتي");

        try {
            startActivity(Intent.createChooser(intent, "اختر تطبيق البريد"));
        } catch (Exception e) {
            Toast.makeText(this, "لا يوجد تطبيق بريد إلكتروني", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("حول التطبيق")
                .setMessage("تطبيق سلتي\n" +
                        "الإصدار: 1.0.0\n\n" +
                        "تطبيق تسوق إلكتروني يوفر لك تجربة تسوق سهلة وممتعة.\n\n" +
                        "جميع الحقوق محفوظة © 2026")
                .setPositiveButton("موافق", null)
                .show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, login.class);
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