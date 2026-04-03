package com.example.silti;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silti.databinding.ActivityLikeyBinding;

import java.util.ArrayList;
import java.util.List;

public class Likey extends AppCompatActivity {

    private ActivityLikeyBinding binding;
    private FavoriteViewModel favoriteViewModel;
    private CartViewModel cartViewModel;
    private ProductViewModel productViewModel;
    private FavoritesAdapter favoritesAdapter;
    private long currentUserId;
    private List<table_faivorate> favoriteList = new ArrayList<>();
    private List<table_product> favoriteProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLikeyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModels();
        getCurrentUser();
        setupRecyclerView();
        setupClickListeners();
        loadFavorites();
    }

    private void initViewModels() {
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
    }

    private void getCurrentUser() {
        SharedPreferences prefs = getSharedPreferences("MyCartPrefs", MODE_PRIVATE);
        currentUserId = prefs.getLong("userId", -1);

        if (currentUserId != -1) {
            favoriteViewModel.setCurrentUserId(currentUserId);
            cartViewModel.setCurrentUserId(currentUserId);
        }
    }

    private void setupRecyclerView() {
        favoritesAdapter = new FavoritesAdapter(new ArrayList<>(), new FavoritesAdapter.OnFavoriteClickListener() {
            @Override
            public void onProductClick(table_product product) {
                Intent intent = new Intent(Likey.this, MainProduct.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            @Override
            public void onRemoveFromFavorites(table_product product) {
                favoriteViewModel.removeFromFavorite(product.getId());
                loadFavorites();
                Toast.makeText(Likey.this, "تمت الإزالة من المفضلة", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToCartClick(table_product product) {
                double price = product.getDiscount() > 0 ?
                        product.getPrice() * (1 - product.getDiscount() / 100) :
                        product.getPrice();

                cartViewModel.addToCart(
                        product.getId(),
                        product.getName(),
                        price,
                        product.getImage(),
                        1,
                        "M"
                );
                Toast.makeText(Likey.this, "تمت الإضافة إلى السلة", Toast.LENGTH_SHORT).show();
            }
        });

        binding.recycleLike.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recycleLike.setAdapter(favoritesAdapter);
    }

    private void setupClickListeners() {
        // زر العودة
        binding.back.setOnClickListener(v -> finish());

        // زر حذف الكل
        binding.clear.setOnClickListener(v -> {
            if (favoriteProducts.isEmpty()) {
                Toast.makeText(this, "لا توجد منتجات في المفضلة", Toast.LENGTH_SHORT).show();
                return;
            }

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("حذف الكل")
                    .setMessage("هل أنت متأكد من حذف جميع المنتجات من المفضلة؟")
                    .setPositiveButton("نعم", (dialog, which) -> {
                        favoriteViewModel.clearAllFavorites();
                        loadFavorites();
                        Toast.makeText(Likey.this, "تم حذف جميع المنتجات من المفضلة", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("لا", null)
                    .show();
        });

        // زر الذهاب للتسوق
        binding.goShopping.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadFavorites() {
        // مراقبة قائمة المفضلة
        favoriteViewModel.getFavoriteItems().observe(this, favorites -> {
            if (favorites != null) {
                favoriteList.clear();
                favoriteList.addAll(favorites);

                if (favoriteList.isEmpty()) {
                    showEmptyState(true);
                    return;
                }

                // جلب تفاصيل المنتجات من الـ IDs
                loadProductDetails();
            }
        });
    }

    private void loadProductDetails() {
        favoriteProducts.clear();

        for (table_faivorate fav : favoriteList) {
            productViewModel.getProductById(fav.getProductId()).observe(this, product -> {
                if (product != null && !favoriteProducts.contains(product)) {
                    favoriteProducts.add(product);
                }

                // تحديث الـ Adapter بعد تحميل جميع المنتجات
                if (favoriteProducts.size() == favoriteList.size()) {
                    favoritesAdapter.updateProducts(favoriteProducts);
                    showEmptyState(false);
                }
            });
        }
    }

    private void showEmptyState(boolean isEmpty) {
        if (isEmpty) {
            binding.emptyLayout.setVisibility(View.VISIBLE);
            binding.recycleLike.setVisibility(View.GONE);
        } else {
            binding.emptyLayout.setVisibility(View.GONE);
            binding.recycleLike.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}