package com.example.silti;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.silti.databinding.ActivityShowProductBinding;

import java.util.ArrayList;
import java.util.List;

public class Show_Product extends AppCompatActivity {

    private ActivityShowProductBinding binding;
    private ProductViewModel productViewModel;
    private FirstCategoryViewModel firstCategoryViewModel;
    private SecondCategoryViewModel secondCategoryViewModel;
    private CategoryAdapter subCategoryAdapter;
    private CategoryAdapter insideCategoryAdapter;
    private ProductAdapter productAdapter;

    private String categoryName;
    private int firstCategoryId = 0;
    private int selectedSecondCategoryId = 0;
    private int selectedInsideCategoryId = 0;

    private List<String> subCategories = new ArrayList<>();
    private List<String> insideCategories = new ArrayList<>();
    private List<table_product> allProducts = new ArrayList<>();
    private List<table_secondCategory> secondCategoriesList = new ArrayList<>();
    private List<table_CategorisInside> insideCategoriesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityShowProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModels();
        getIntentData();
        setupRecyclerViews();
        setupClickListeners();
    }

    private void initViewModels() {
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        firstCategoryViewModel = new ViewModelProvider(this).get(FirstCategoryViewModel.class);
        secondCategoryViewModel = new ViewModelProvider(this).get(SecondCategoryViewModel.class);
    }

    private void getIntentData() {
        categoryName = getIntent().getStringExtra("category_name");
        if (categoryName == null || categoryName.isEmpty()) {
            Toast.makeText(this, "خطأ: لم يتم تحديد التصنيف", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.titleCategory.setText(categoryName);
        Log.d("Show_Product", "تم استلام التصنيف: " + categoryName);

        // البحث عن معرف التصنيف الأساسي
        findFirstCategoryId();
    }

    private void findFirstCategoryId() {
        firstCategoryViewModel.getAllActiveCategories().observe(this, categories -> {
            if (categories != null) {
                for (table_firstCategory cat : categories) {
                    if (cat.name.equals(categoryName)) {
                        firstCategoryId = cat.id;
                        Log.d("Show_Product", "تم العثور على معرف التصنيف: " + firstCategoryId + " للتصنيف: " + categoryName);
                        loadData();
                        break;
                    }
                }

                if (firstCategoryId == 0) {
                    Toast.makeText(this, "لم يتم العثور على التصنيف المطلوب", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void setupRecyclerViews() {
        // Adapter للتصنيفات الفرعية
        subCategoryAdapter = new CategoryAdapter(new ArrayList<>(), (category, position) -> {
            loadInsideCategories(category);
        });
        binding.rvSubCategories.setAdapter(subCategoryAdapter);

        // Adapter للتصنيفات الداخلية
        insideCategoryAdapter = new CategoryAdapter(new ArrayList<>(), (category, position) -> {
            loadProductsForSelectedCategories();
        });
        binding.rvInsideCategories.setAdapter(insideCategoryAdapter);

        // Adapter للمنتجات
        productAdapter = new ProductAdapter(new ArrayList<>(), new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(table_product product) {
                Intent intent = new Intent(Show_Product.this, MainProduct.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(table_product product, boolean isFavorite) {
                Toast.makeText(Show_Product.this,
                        "تم " + (isFavorite ? "إزالة من" : "إضافة إلى") + " المفضلة",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToCartClick(table_product product) {
                Toast.makeText(Show_Product.this, "تمت الإضافة إلى السلة", Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvProducts.setAdapter(productAdapter);
    }

    private void setupClickListeners() {
        binding.back.setOnClickListener(v -> finish());

        binding.searchInCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    searchProducts(s.toString());
                } else if (s.length() == 0) {
                    loadProductsForSelectedCategories();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadData() {
        // ✅ تعيين معرف التصنيف الأساسي في ProductViewModel
        productViewModel.setCurrentFirstCategoryId(firstCategoryId);

        // ✅ مراقبة المنتجات (بدون معاملات)
        productViewModel.getProductsByFirstCategory().observe(this, products -> {
            if (products != null) {
                allProducts.clear();
                allProducts.addAll(products);
                Log.d("Show_Product", "تم تحميل " + products.size() + " منتج للتصنيف: " + categoryName);

                if (allProducts.isEmpty()) {
                    Toast.makeText(this, "لا توجد منتجات في هذا التصنيف", Toast.LENGTH_SHORT).show();
                }

                loadSubCategories();
            }
        });

        // ✅ تعيين معرف التصنيف الأساسي في SecondCategoryViewModel
        secondCategoryViewModel.setCurrentFirstCategoryId(firstCategoryId);

        // ✅ مراقبة التصنيفات الفرعية (بدون معاملات)
        secondCategoryViewModel.getCategoriesByFirstCategory().observe(this, secondCategories -> {
            if (secondCategories != null) {
                secondCategoriesList.clear();
                secondCategoriesList.addAll(secondCategories);
                Log.d("Show_Product", "تم تحميل " + secondCategories.size() + " تصنيف فرعي");
            }
        });
    }

    private void loadSubCategories() {
        subCategories.clear();

        // استخدام أسماء التصنيفات الفرعية من secondCategoriesList
        if (!secondCategoriesList.isEmpty()) {
            for (table_secondCategory secondCat : secondCategoriesList) {
                subCategories.add(secondCat.name);
            }
        } else {
            // إذا لم تكن هناك تصنيفات فرعية، استخرج من المنتجات
            for (table_product product : allProducts) {
                if (product.getSecondCategoryId() > 0) {
                    String subCatName = "تصنيف " + product.getSecondCategoryId();
                    if (!subCategories.contains(subCatName)) {
                        subCategories.add(subCatName);
                    }
                }
            }
        }

        if (subCategories.isEmpty()) {
            subCategories.add("الكل");
        }

        subCategoryAdapter.updateCategories(subCategories);
        Log.d("Show_Product", "تم عرض " + subCategories.size() + " تصنيف فرعي");

        if (!subCategories.isEmpty()) {
            loadInsideCategories(subCategories.get(0));
        }
    }

    private void loadInsideCategories(String subCategory) {
        insideCategories.clear();

        // تحديد معرف التصنيف الفرعي المحدد
        selectedSecondCategoryId = subCategories.indexOf(subCategory) + 1;

        // استخراج التصنيفات الداخلية من المنتجات
        for (table_product product : allProducts) {
            if (product.getSecondCategoryId() == selectedSecondCategoryId &&
                    product.getInsideCategoryId() > 0) {

                String insideCat = "قسم " + product.getInsideCategoryId();
                if (!insideCategories.contains(insideCat)) {
                    insideCategories.add(insideCat);
                }
            }
        }

        if (insideCategories.isEmpty()) {
            insideCategories.add("الكل");
        }

        insideCategoryAdapter.updateCategories(insideCategories);
        Log.d("Show_Product", "تم عرض " + insideCategories.size() + " تصنيف داخلي");

        loadProductsForSelectedCategories();
    }

    private void loadProductsForSelectedCategories() {
        List<table_product> filteredProducts = new ArrayList<>();

        for (table_product product : allProducts) {
            // فلترة حسب التصنيف الفرعي
            if (selectedSecondCategoryId == 0 || product.getSecondCategoryId() == selectedSecondCategoryId) {

                // فلترة حسب التصنيف الداخلي (إذا تم اختيار واحد)
                if (selectedInsideCategoryId == 0 || product.getInsideCategoryId() == selectedInsideCategoryId) {
                    filteredProducts.add(product);
                }
            }
        }

        productAdapter.updateProducts(filteredProducts);
        Log.d("Show_Product", "تم عرض " + filteredProducts.size() + " منتج");

        if (filteredProducts.isEmpty()) {
            Toast.makeText(this, "لا توجد منتجات", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchProducts(String query) {
        List<table_product> searchResults = new ArrayList<>();

        for (table_product product : allProducts) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(product);
            }
        }

        productAdapter.updateProducts(searchResults);
        Log.d("Show_Product", "نتائج البحث: " + searchResults.size() + " منتج");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}