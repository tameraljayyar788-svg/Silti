package com.example.silti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.silti.databinding.ActivityChoosePayMentBinding;

public class ChoosePayMent extends AppCompatActivity {

    private ActivityChoosePayMentBinding binding;
    private SharedPreferences orderPrefs;
    private boolean isCashSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChoosePayMentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderPrefs = getSharedPreferences("OrderPrefs", MODE_PRIVATE);

        setupClickListeners();
        setupCardToggle();
        loadOrderData();

        // ✅ التأكد من ظهور خيار الدفع كاش
        binding.cashLayout.setVisibility(View.VISIBLE);
    }

    private void setupClickListeners() {
        // زر العودة
        binding.back.setOnClickListener(v -> finish());

        // تبديل طريقة الدفع النقدي
        binding.cash.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isCashSelected = isChecked;
            if (isChecked) {
                Toast.makeText(this, "تم اختيار الدفع كاش عند التوصيل", Toast.LENGTH_SHORT).show();
            }
        });

        // زر التأكيد
        binding.agree.setOnClickListener(v -> {
            if (isCashSelected) {
                completeOrder("cash");
            } else {
                Toast.makeText(this, "الرجاء اختيار طريقة الدفع", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCardToggle() {
        // إظهار/إخفاء حقل إضافة بطاقة
        binding.add.setOnClickListener(v -> {
            if (binding.dataCard.getVisibility() == View.GONE) {
                binding.dataCard.setVisibility(View.VISIBLE);
                // إلغاء تحديد الدفع النقدي عند إضافة بطاقة
                binding.cash.setChecked(false);
                isCashSelected = false;
            } else {
                binding.dataCard.setVisibility(View.GONE);
            }
        });
    }

    private void loadOrderData() {
        String fullName = orderPrefs.getString("full_name", "");
        double totalAmount = orderPrefs.getFloat("total_amount", 0);

        // يمكن عرض اسم المستخدم والمبلغ
    }

    private void completeOrder(String paymentMethod) {
        Toast.makeText(this, "تم إتمام الطلب بنجاح!\n" +
                "طريقة الدفع: كاش عند التوصيل\n" +
                "سيتم التواصل معك قريباً", Toast.LENGTH_LONG).show();

        clearCart();

        Intent intent = new Intent(this, Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void clearCart() {
        SharedPreferences prefs = getSharedPreferences("MyCartPrefs", MODE_PRIVATE);
        long userId = prefs.getLong("userId", -1);

        if (userId != -1) {
            CartViewModel cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
            cartViewModel.setCurrentUserId(userId);
            cartViewModel.clearCart();
        }

        orderPrefs.edit().clear().apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}