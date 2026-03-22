package com.example.silti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.silti.databinding.ActivityMainProductBinding;

public class MainProduct extends AppCompatActivity {

    private ActivityMainProductBinding binding;
    private ProductViewModel productViewModel;
    private FavoriteViewModel favoriteViewModel;
    private CartViewModel cartViewModel;
    private ProductDetailsAdapter detailsAdapter;

    private long productId;
    private table_product currentProduct;
    private long currentUserId;
    private boolean isFavorite = false;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModels();
        getIntentData();
        getCurrentUser();
        setupRecyclerView();
        setupClickListeners();
        loadProductData();
    }

    private void initViewModels() {
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }

    private void getIntentData() {
        productId = getIntent().getLongExtra("product_id", -1);
        if (productId == -1) {
            Toast.makeText(this, "خطأ في تحميل المنتج", Toast.LENGTH_SHORT).show();
            finish();
        }
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
        detailsAdapter = new ProductDetailsAdapter();
        binding.dataProduct.setLayoutManager(new LinearLayoutManager(this));
        binding.dataProduct.setAdapter(detailsAdapter);
    }

    private void setupClickListeners() {
        binding.back.setOnClickListener(v -> finish());

        binding.cart.setOnClickListener(v -> {
            startActivity(new Intent(this, Cart.class));
        });

        binding.favoriteBtn.setOnClickListener(v -> {
            toggleFavorite();
        });

        binding.plus.setOnClickListener(v -> {
            quantity++;
            binding.quantity.setText(String.valueOf(quantity));
        });

        binding.minus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.quantity.setText(String.valueOf(quantity));
            }
        });

        binding.buttonSell.setOnClickListener(v -> {
            buyNow();
        });

        binding.addToCart.setOnClickListener(v -> {
            addToCart();
        });
    }

    private void loadProductData() {
        productViewModel.getProductById(productId).observe(this, product -> {
            if (product != null) {
                currentProduct = product;
                displayProductDetails();
                checkIfFavorite();
            } else {
                Toast.makeText(this, "المنتج غير موجود", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayProductDetails() {
        binding.nameProduct.setText(currentProduct.getName());

        // ✅ تحميل صورة المنتج مع تحسين الأداء
        if (currentProduct.getImage() != null && !currentProduct.getImage().isEmpty()) {
            Glide.with(this)
                    .load(currentProduct.getImage())
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imgProduct);
        } else {
            binding.imgProduct.setImageResource(R.drawable.logo);
        }

        binding.ratingBar.setRating(currentProduct.getRate());

        // عرض السعر والخصم
        if (currentProduct.getDiscount() > 0) {
            binding.regularPrice.setVisibility(View.GONE);
            binding.currencyRegular.setVisibility(View.GONE);

            binding.oldPrice.setVisibility(View.VISIBLE);
            binding.strikeLine.setVisibility(View.VISIBLE);
            binding.newPrice.setVisibility(View.VISIBLE);
            binding.currencyNew.setVisibility(View.VISIBLE);

            binding.oldPrice.setText(String.valueOf((int) currentProduct.getPrice()));
            double discountedPrice = currentProduct.getPrice() * (1 - currentProduct.getDiscount() / 100);
            binding.newPrice.setText(String.valueOf((int) discountedPrice));

            binding.discountBadge.setVisibility(View.VISIBLE);
            binding.tvDiscountPercent.setText(String.format("%.0f%%", currentProduct.getDiscount()));
        } else {
            binding.discountBadge.setVisibility(View.GONE);
            binding.oldPrice.setVisibility(View.GONE);
            binding.strikeLine.setVisibility(View.GONE);
            binding.newPrice.setVisibility(View.GONE);
            binding.currencyNew.setVisibility(View.GONE);

            binding.regularPrice.setVisibility(View.VISIBLE);
            binding.currencyRegular.setVisibility(View.VISIBLE);
            binding.regularPrice.setText(String.valueOf((int) currentProduct.getPrice()));
        }

        if (detailsAdapter != null) {
            detailsAdapter.setDetails(currentProduct);
        }
    }

    private void checkIfFavorite() {
        if (currentUserId != -1 && currentProduct != null) {
            favoriteViewModel.isFavorite(currentUserId, currentProduct.getId(), isFav -> {
                isFavorite = isFav;
                updateFavoriteButton();
            });
        }
    }

    private void updateFavoriteButton() {
        if (isFavorite) {
            binding.favoriteBtn.setImageResource(R.drawable.like_red);
        } else {
            binding.favoriteBtn.setImageResource(R.drawable.like_black);
        }
    }

    private void toggleFavorite() {
        if (currentUserId == -1) {
            Toast.makeText(this, "الرجاء تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isFavorite) {
            favoriteViewModel.removeFromFavorite(currentProduct.getId());
            Toast.makeText(this, "تمت الإزالة من المفضلة", Toast.LENGTH_SHORT).show();
        } else {
            favoriteViewModel.addToFavorite(currentProduct.getId());
            Toast.makeText(this, "تمت الإضافة إلى المفضلة", Toast.LENGTH_SHORT).show();
        }
        isFavorite = !isFavorite;
        updateFavoriteButton();
    }

    private void addToCart() {
        if (currentUserId == -1) {
            Toast.makeText(this, "الرجاء تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = currentProduct.getDiscount() > 0 ?
                currentProduct.getPrice() * (1 - currentProduct.getDiscount() / 100) :
                currentProduct.getPrice();

        // ✅ تمرير مسار الصورة بشكل صحيح
        cartViewModel.addToCart(
                currentProduct.getId(),
                currentProduct.getName(),
                price,
                currentProduct.getImage(),  // مسار الصورة
                quantity,
                "M"
        );

        Toast.makeText(this, "تمت الإضافة إلى السلة", Toast.LENGTH_SHORT).show();
    }

    private void buyNow() {
        if (currentUserId == -1) {
            Toast.makeText(this, "الرجاء تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        addToCart();
        Intent intent = new Intent(this, PaymentMethod.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}