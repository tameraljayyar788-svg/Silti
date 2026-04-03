package com.example.silti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.silti.databinding.ActivityPaymentMethodBinding;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethod extends AppCompatActivity {

    private ActivityPaymentMethodBinding binding;
    private CartViewModel cartViewModel;
    private long currentUserId;
    private double totalAmount;

    // قائمة المدن الفلسطينية
    private final String[] palestinianCities = {
            "القدس", "رام الله", "البيرة", "بيت لحم", "الخليل", "نابلس", "جنين",
            "طولكرم", "قلقيلية", "سلفيت", "أريحا", "غزة", "خان يونس", "رفح"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPaymentMethodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModels();
        getCurrentUser();
        setupSpinner();
        setupClickListeners();
        loadCartTotal();
    }

    private void initViewModels() {
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }

    private void getCurrentUser() {
        SharedPreferences prefs = getSharedPreferences("MyCartPrefs", MODE_PRIVATE);
        currentUserId = prefs.getLong("userId", -1);

        if (currentUserId != -1) {
            cartViewModel.setCurrentUserId(currentUserId);
        }
    }

    private void setupSpinner() {
        // ✅ إعداد Spinner للمدن الفلسطينية مع عرض النص
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, palestinianCities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerCountry.setAdapter(adapter);

        // ✅ إضافة مستمع لاختيار المدينة
        binding.spinerCountry.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = palestinianCities[position];
                // يمكن عرض المدينة المختارة
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void setupClickListeners() {
        // زر العودة
        binding.back.setOnClickListener(v -> finish());

        // ✅ اختيار طريقة الدفع - فتح شاشة اختيار الدفع
        binding.ChoosepaymentMethode.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentMethod.this, ChoosePayMent.class);
            startActivity(intent);
        });

        // زر التأكيد
        binding.agree.setOnClickListener(v -> {
            if (validateFields()) {
                processOrder();
            }
        });
    }

    private void loadCartTotal() {
        cartViewModel.getCartTotal().observe(this, total -> {
            if (total != null) {
                totalAmount = total;
            }
        });
    }

    private boolean validateFields() {
        String firstName = binding.firstName.getText().toString().trim();
        String lastName = binding.lastName.getText().toString().trim();
        String phone = binding.phone.getText().toString().trim();
        String email = binding.email.getText().toString().trim();
        String city = binding.city.getText().toString().trim();
        String address1 = binding.location1.getText().toString().trim();

        if (firstName.isEmpty()) {
            binding.firstName.setError("الاسم الأول مطلوب");
            binding.firstName.requestFocus();
            return false;
        }

        if (lastName.isEmpty()) {
            binding.lastName.setError("الاسم الأخير مطلوب");
            binding.lastName.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            binding.phone.setError("رقم الهاتف مطلوب");
            binding.phone.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            binding.email.setError("البريد الإلكتروني مطلوب");
            binding.email.requestFocus();
            return false;
        }

        if (city.isEmpty()) {
            binding.city.setError("الحي / المنطقة مطلوب");
            binding.city.requestFocus();
            return false;
        }

        if (address1.isEmpty()) {
            binding.location1.setError("العنوان مطلوب");
            binding.location1.requestFocus();
            return false;
        }

        return true;
    }

    private void processOrder() {
        // الحصول على البيانات
        String firstName = binding.firstName.getText().toString().trim();
        String lastName = binding.lastName.getText().toString().trim();
        String fullName = firstName + " " + lastName;
        String phone = binding.phone.getText().toString().trim();
        String email = binding.email.getText().toString().trim();
        String city = binding.city.getText().toString().trim();
        String address1 = binding.location1.getText().toString().trim();
        String address2 = binding.location2.getText().toString().trim();
        String fullAddress = address1 + (address2.isEmpty() ? "" : "، " + address2);

        // المدينة المختارة من Spinner
        String selectedCity = binding.spinerCountry.getSelectedItem().toString();

        // حفظ البيانات في SharedPreferences
        SharedPreferences prefs = getSharedPreferences("OrderPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("full_name", fullName);
        editor.putString("phone", phone);
        editor.putString("email", email);
        editor.putString("city", city);
        editor.putString("selected_city", selectedCity);
        editor.putString("address", fullAddress);
        editor.putFloat("total_amount", (float) totalAmount);
        editor.apply();

        // الانتقال إلى شاشة اختيار طريقة الدفع
        Intent intent = new Intent(PaymentMethod.this, ChoosePayMent.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}