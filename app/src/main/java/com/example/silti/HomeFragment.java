package com.example.silti;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.silti.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    // ViewModels
    private ProductViewModel productViewModel;
    private FavoriteViewModel favoriteViewModel;
    private CartViewModel cartViewModel;

    // Adapters
    private ProductAdapter recommendedAdapter;

    // Data lists
    private List<table_product> allProducts = new ArrayList<>();
    private List<table_faivorate> favoritesList = new ArrayList<>();

    // المنتج المعروض في الصورة الرئيسية
    private table_product currentMainOfferProduct;

    // User data
    private long currentUserId;

    // For discount rotation
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private ScheduledExecutorService scheduler;

    // مصفوفات لربط التصنيفات
    private final int[] CATEGORY_VIEWS = {
            R.id.prety, R.id.fation, R.id.home,
            R.id.elictronic, R.id.books, R.id.sport
    };

    private final String[] CATEGORY_NAMES = {
            "الجمال", "موضة", "بيت", "إلكترونيات", "كُتب", "رياضة"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModels();
        getCurrentUser();
        setupRecyclerView();
        setupClickListeners();
        loadData();
        startMainOfferRotation();
    }

    private void initViewModels() {
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        favoriteViewModel = new ViewModelProvider(requireActivity()).get(FavoriteViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
    }

    private void getCurrentUser() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyCartPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getLong("userId", -1);

        if (currentUserId != -1) {
            favoriteViewModel.setCurrentUserId(currentUserId);
            cartViewModel.setCurrentUserId(currentUserId);
        }
    }

    private void setupRecyclerView() {
        // RecyclerView للمنتجات المقترحة
        recommendedAdapter = new ProductAdapter(new ArrayList<>(), new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(table_product product) {
                openProductDetails(product);
            }

            @Override
            public void onFavoriteClick(table_product product, boolean isFavorite) {
                handleFavoriteClick(product, isFavorite);
            }

            @Override
            public void onAddToCartClick(table_product product) {
                handleAddToCart(product);
            }
        });

        if (binding.rvRecommended != null) {
            binding.rvRecommended.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.rvRecommended.setAdapter(recommendedAdapter);
        }
    }

    private void setupClickListeners() {
        // بحث
        binding.search.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), Search.class));
        });

        // ========== صورة العرض الرئيسية - تعرض منتج عشوائي عليه خصم ==========
        binding.offersMain.setOnClickListener(v -> {
            if (currentMainOfferProduct != null) {
                openProductDetails(currentMainOfferProduct);
            } else {
                Toast.makeText(requireContext(), "لا توجد عروض حالياً", Toast.LENGTH_SHORT).show();
            }
        });

        // أيقونة الإشعارات
        binding.notification.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), Notifications.class));
        });

        // التصنيفات الرئيسية
        for (int i = 0; i < CATEGORY_VIEWS.length; i++) {
            final int index = i;
            View categoryView = binding.getRoot().findViewById(CATEGORY_VIEWS[i]);
            if (categoryView != null) {
                categoryView.setOnClickListener(v -> {
                    openCategory(CATEGORY_NAMES[index]);
                });
            }
        }
    }

    private void loadData() {
        // تحميل جميع المنتجات
        productViewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                allProducts.clear();
                allProducts.addAll(products);

                // تحديث المنتجات المقترحة
                updateRecommendedProducts();

                // تحديث الصورة الرئيسية إذا لم تكن محددة
                if (currentMainOfferProduct == null && !allProducts.isEmpty()) {
                    selectRandomProductForMainOffer();
                }
            }
        });

        // تحميل قائمة المفضلة
        if (currentUserId != -1) {
            favoriteViewModel.getFavoriteItems().observe(getViewLifecycleOwner(), favorites -> {
                if (favorites != null) {
                    favoritesList.clear();
                    favoritesList.addAll(favorites);

                    // تحديث حالة الإعجاب في الـ Adapter
                    if (recommendedAdapter != null) {
                        recommendedAdapter.updateFavorites(favoritesList);
                    }
                }
            });
        }
    }

    private void openProductDetails(table_product product) {
        Intent intent = new Intent(requireContext(), MainProduct.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }

    private void handleFavoriteClick(table_product product, boolean isFavorite) {
        if (currentUserId != -1) {
            if (isFavorite) {
                favoriteViewModel.removeFromFavorite(product.getId());
                Toast.makeText(requireContext(), "تمت الإزالة من المفضلة", Toast.LENGTH_SHORT).show();
            } else {
                favoriteViewModel.addToFavorite(product.getId());
                Toast.makeText(requireContext(), "تمت الإضافة إلى المفضلة", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "الرجاء تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAddToCart(table_product product) {
        if (currentUserId != -1) {
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

            Toast.makeText(requireContext(), "تمت الإضافة إلى السلة", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "الرجاء تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCategory(String categoryName) {
        Intent intent = new Intent(requireContext(), Show_Product.class);
        intent.putExtra("category_name", categoryName);
        startActivity(intent);
    }

    // ========== نظام تدوير الصورة الرئيسية ==========

    private void startMainOfferRotation() {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        // تشغيل التحديث كل 4 ساعات (14400 ثانية)
        scheduler.scheduleAtFixedRate(() -> {
            selectRandomProductForMainOffer();
        }, 0, 4, TimeUnit.HOURS);

        // للاختبار: استخدم 10 ثواني
        // scheduler.scheduleAtFixedRate(() -> {
        //     selectRandomProductForMainOffer();
        // }, 0, 10, TimeUnit.SECONDS);
    }

    private void selectRandomProductForMainOffer() {
        if (allProducts.isEmpty()) return;

        // اختيار منتج عشوائي من جميع المنتجات
        Random random = new Random();
        int randomIndex = random.nextInt(allProducts.size());
        currentMainOfferProduct = allProducts.get(randomIndex);

        // تحديث واجهة المستخدم في الخيط الرئيسي
        mainHandler.post(() -> {
            updateMainOfferImage(currentMainOfferProduct);
        });
    }

    private void updateRecommendedProducts() {
        // اختيار 5 منتجات عشوائية كمنتجات مقترحة
        if (allProducts.size() <= 5) {
            if (recommendedAdapter != null) {
                recommendedAdapter.updateProducts(allProducts);
            }
            return;
        }

        Random random = new Random();
        List<table_product> recommended = new ArrayList<>();
        List<Integer> selectedIndices = new ArrayList<>();

        while (recommended.size() < 5) {
            int index = random.nextInt(allProducts.size());
            if (!selectedIndices.contains(index)) {
                selectedIndices.add(index);
                recommended.add(allProducts.get(index));
            }

            // منع التكرار اللانهائي
            if (selectedIndices.size() >= allProducts.size()) break;
        }

        if (recommendedAdapter != null) {
            recommendedAdapter.updateProducts(recommended);
        }
    }

    private void updateMainOfferImage(table_product product) {
        if (product == null) return;

        // تحديث صورة العرض الرئيسي
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            Glide.with(requireContext())
                    .load(product.getImage())
                    .placeholder(R.drawable.newoffer)
                    .error(R.drawable.newoffer)
                    .into(binding.offersMain);
        } else {
            binding.offersMain.setImageResource(R.drawable.newoffer);
        }

        // إضافة نص المنتج كـ ContentDescription
        binding.offersMain.setContentDescription(product.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        // تحديث المفضلة عند العودة للشاشة
        if (currentUserId != -1 && favoriteViewModel != null) {
            favoriteViewModel.getFavoriteItems().removeObservers(getViewLifecycleOwner());
            favoriteViewModel.getFavoriteItems().observe(getViewLifecycleOwner(), favorites -> {
                if (favorites != null) {
                    favoritesList.clear();
                    favoritesList.addAll(favorites);

                    if (recommendedAdapter != null) {
                        recommendedAdapter.updateFavorites(favoritesList);
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (scheduler != null) {
            scheduler.shutdown();
        }
        mainHandler.removeCallbacksAndMessages(null);
        binding = null;
    }
}