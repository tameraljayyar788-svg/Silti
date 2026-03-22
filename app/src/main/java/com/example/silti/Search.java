package com.example.silti;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silti.databinding.ActivitySearchBinding;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private ProductViewModel productViewModel;
    private ProductAdapter productAdapter;
    private List<table_product> allProducts = new ArrayList<>();
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModels();
        setupRecyclerView();
        setupClickListeners();
        setupSearchListener();
        loadAllProducts();
    }

    private void initViewModels() {
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(new ArrayList<>(), new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(table_product product) {
                Intent intent = new Intent(Search.this, MainProduct.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(table_product product, boolean isFavorite) {
                Toast.makeText(Search.this,
                        "تم " + (isFavorite ? "إزالة من" : "إضافة إلى") + " المفضلة",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToCartClick(table_product product) {
                Toast.makeText(Search.this, "تمت الإضافة إلى السلة", Toast.LENGTH_SHORT).show();
            }
        });

        binding.recyclerSearch.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerSearch.setAdapter(productAdapter);
    }

    private void setupClickListeners() {
        binding.back.setOnClickListener(v -> finish());
    }

    private void setupSearchListener() {
        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString();
                if (currentQuery.length() >= 2) {
                    performSearch(currentQuery);
                } else if (currentQuery.isEmpty()) {
                    productAdapter.updateProducts(new ArrayList<>());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadAllProducts() {
        productViewModel.getAllProducts().observe(this, products -> {
            if (products != null) {
                allProducts.clear();
                allProducts.addAll(products);
            }
        });
    }

    private void performSearch(String query) {
        // البحث في جميع المنتجات
        List<table_product> searchResults = new ArrayList<>();

        for (table_product product : allProducts) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(product);
            }
        }
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Search.this, HomeFragment.class);
                startActivity(intent);
            }
        });

        productAdapter.updateProducts(searchResults);

        if (searchResults.isEmpty()) {
            Toast.makeText(this, "لا توجد نتائج", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}