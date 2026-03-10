package com.example.silti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.silti.databinding.ActivityAdmenBinding;

public class Admen extends AppCompatActivity {

    private ActivityAdmenBinding binding;

    private FirstCategoryViewModel firstCategoryViewModel;
    private SecondCategoryViewModel secondCategoryViewModel;
    private CategorisInsideViewModel insideCategoryViewModel;
    private ProductViewModel productViewModel;

    private long currentAdminId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdmenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModels();
        setupClickListeners();
        loadSpinners();

        // الحصول على ID المدير من SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyCartPrefs", MODE_PRIVATE);
        currentAdminId = prefs.getLong("userId", 1);
    }

    private void initViewModels() {
        firstCategoryViewModel = new ViewModelProvider(this).get(FirstCategoryViewModel.class);
        secondCategoryViewModel = new ViewModelProvider(this).get(SecondCategoryViewModel.class);
        insideCategoryViewModel = new ViewModelProvider(this).get(CategorisInsideViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
    }

    private void setupClickListeners() {
        // Logout
        binding.logout.setOnClickListener(v -> logout());

        // Toggle cards
        binding.more1.setOnClickListener(v -> toggleCard(binding.card1));
        binding.more2.setOnClickListener(v -> toggleCard(binding.card2));
        binding.more3.setOnClickListener(v -> toggleCard(binding.card3));
        binding.more4.setOnClickListener(v -> toggleCard(binding.card4));

        // Add buttons
        binding.addCategorySecond.setOnClickListener(v -> addSecondCategory());
        binding.addCategoryIn.setOnClickListener(v -> addInsideCategory());
        binding.addProduct.setOnClickListener(v -> addProduct());

        // Category selection
        binding.spinerFirstCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                table_firstCategory selected = (table_firstCategory) parent.getItemAtPosition(position);
                if (selected != null) {
                    secondCategoryViewModel.setCurrentFirstCategoryId(selected.id);
                    loadSecondCategoriesForSpinner();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        binding.spinerCategorySecond.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                table_secondCategory selected = (table_secondCategory) parent.getItemAtPosition(position);
                if (selected != null) {
                    insideCategoryViewModel.setCurrentSecondCategoryId(selected.id);
                    loadInsideCategoriesForSpinner();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void toggleCard(View card) {
        card.setVisibility(card.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private void loadSpinners() {
        // Load First Categories
        firstCategoryViewModel.getAllActiveCategories().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                ArrayAdapter<table_firstCategory> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinerFirstCategory.setAdapter(adapter);
                binding.spinerFirstForProduct.setAdapter(adapter);
            }
        });
    }

    private void loadSecondCategoriesForSpinner() {
        secondCategoryViewModel.getCategoriesByFirstCategory().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                ArrayAdapter<table_secondCategory> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinerCategorySecond.setAdapter(adapter);
                binding.spinerSecondForCategory.setAdapter(adapter);
            }
        });
    }

    private void loadInsideCategoriesForSpinner() {
        insideCategoryViewModel.getCategoriesBySecondCategory().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                ArrayAdapter<table_CategorisInside> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinerCategoryIn.setAdapter(adapter);
            }
        });
    }

    private void addSecondCategory() {
        String name = binding.nameCategorySecond.getText().toString().trim();
        table_firstCategory selected = (table_firstCategory) binding.spinerFirstCategory.getSelectedItem();

        if (name.isEmpty() || selected == null) {
            Toast.makeText(this, "الرجاء إدخال الاسم واختيار التصنيف", Toast.LENGTH_SHORT).show();
            return;
        }

        secondCategoryViewModel.insert(name, selected.icon);
        Toast.makeText(this, "تم إضافة التصنيف الفرعي", Toast.LENGTH_SHORT).show();
        binding.nameCategorySecond.setText("");
    }

    private void addInsideCategory() {
        String name = binding.nameCategoryIn.getText().toString().trim();
        table_secondCategory selected = (table_secondCategory) binding.spinerCategorySecond.getSelectedItem();

        if (name.isEmpty() || selected == null) {
            Toast.makeText(this, "الرجاء إدخال الاسم واختيار التصنيف", Toast.LENGTH_SHORT).show();
            return;
        }

        insideCategoryViewModel.insert(name, "");
        Toast.makeText(this, "تم إضافة التصنيف الداخلي", Toast.LENGTH_SHORT).show();
        binding.nameCategoryIn.setText("");
    }

    private void addProduct() {
        String name = binding.nameProduct.getText().toString().trim();
        String priceStr = binding.PriceProduct.getText().toString().trim();
        String rateStr = binding.rateProduct.getText().toString().trim();
        String description = binding.discribtion.getText().toString().trim();

        table_firstCategory firstSelected = (table_firstCategory) binding.spinerFirstForProduct.getSelectedItem();
        table_secondCategory secondSelected = (table_secondCategory) binding.spinerSecondForCategory.getSelectedItem();
        table_CategorisInside insideSelected = (table_CategorisInside) binding.spinerCategoryIn.getSelectedItem();

        if (name.isEmpty() || priceStr.isEmpty() || firstSelected == null) {
            Toast.makeText(this, "الرجاء إدخال البيانات المطلوبة", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int rate = rateStr.isEmpty() ? 0 : Integer.parseInt(rateStr);

            table_product product = new table_product();
            product.setName(name);
            product.setPrice(price);
            product.setRate(rate);
            product.setDescription(description);
            product.setFirstCategoryId(firstSelected.id);
            product.setSecondCategoryId(secondSelected != null ? secondSelected.id : 0);
            product.setInsideCategoryId(insideSelected != null ? insideSelected.id : 0);
            product.setActive(true);
            product.setFeatured(false);
            product.setQuantity(100);
            product.setImage("");

            // إضافة المقاسات المحددة
            productViewModel.insertProduct(product);

            Toast.makeText(this, "تم إضافة المنتج بنجاح", Toast.LENGTH_SHORT).show();
            clearProductFields();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "الرجاء إدخال أرقام صحيحة", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearProductFields() {
        binding.nameProduct.setText("");
        binding.PriceProduct.setText("");
        binding.rateProduct.setText("");
        binding.discribtion.setText("");
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("MyCartPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

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