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
    private int selectedSecondCategoryPosition = 0;
    private int selectedInsideCategoryPosition = 0;

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
        // Adapter للتصنيفات الفرعية - مع دعم تغيير اللون عند التحديد
        subCategoryAdapter = new CategoryAdapter(new ArrayList<>(), new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(String category, int position) {
                // تحديث الموضع المحدد
                selectedSecondCategoryPosition = position;
                // تحميل التصنيفات الداخلية
                loadInsideCategories(position);
            }
        });

        binding.rvSubCategories.setAdapter(subCategoryAdapter);

        // Adapter للتصنيفات الداخلية - مع دعم تغيير اللون عند التحديد
        insideCategoryAdapter = new CategoryAdapter(new ArrayList<>(), new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(String category, int position) {
                // تحديث الموضع المحدد
                selectedInsideCategoryPosition = position;
                // تحميل المنتجات حسب التصنيفات المحددة
                loadProductsForSelectedCategories();
            }
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
        // تعيين معرف التصنيف الأساسي في ProductViewModel
        productViewModel.setCurrentFirstCategoryId(firstCategoryId);

        // مراقبة المنتجات
        productViewModel.getProductsByFirstCategory().observe(this, products -> {
            if (products != null) {
                allProducts.clear();
                allProducts.addAll(products);
                Log.d("Show_Product", "تم تحميل " + products.size() + " منتج للتصنيف: " + categoryName);

                if (allProducts.isEmpty()) {
                    Toast.makeText(this, "لا توجد منتجات في هذا التصنيف", Toast.LENGTH_SHORT).show();
                }

                // تحميل التصنيفات الفرعية بعد تحميل المنتجات
                loadSecondCategories();
            }
        });
    }

    private void loadSecondCategories() {
        secondCategoryViewModel.setCurrentFirstCategoryIdForCard3(firstCategoryId);

        secondCategoryViewModel.getCategoriesByFirstCategoryForCard3().observe(this, secondCategories -> {
            if (secondCategories != null && !secondCategories.isEmpty()) {
                secondCategoriesList.clear();
                secondCategoriesList.addAll(secondCategories);

                // إنشاء قائمة بأسماء التصنيفات الفرعية
                List<String> subCategoryNames = new ArrayList<>();
                for (table_secondCategory category : secondCategoriesList) {
                    subCategoryNames.add(category.name);
                }

                // تحديث الـ Adapter للتصنيفات الفرعية
                subCategoryAdapter.updateCategories(subCategoryNames);

                // تحديد أول تصنيف فرعي افتراضياً
                if (!subCategoryNames.isEmpty()) {
                    selectedSecondCategoryPosition = 0;
                    subCategoryAdapter.setSelectedPosition(0);

                    // تحميل التصنيفات الداخلية لأول تصنيف فرعي
                    loadInsideCategories(0);
                }

                Log.d("Show_Product", "تم تحميل " + secondCategories.size() + " تصنيف فرعي");
            } else {
                // إذا لم توجد تصنيفات فرعية، نعرض "الكل" فقط
                List<String> defaultList = new ArrayList<>();
                defaultList.add("الكل");
                subCategoryAdapter.updateCategories(defaultList);
                selectedSecondCategoryPosition = 0;
                subCategoryAdapter.setSelectedPosition(0);

                // تحميل المنتجات مباشرة
                loadProductsForSelectedCategories();
            }
        });
    }

    private void loadInsideCategories(int secondCategoryPosition) {
        if (secondCategoriesList.isEmpty()) {
            // إذا لم توجد تصنيفات فرعية، نظهر "الكل" فقط في التصنيفات الداخلية أيضاً
            List<String> defaultList = new ArrayList<>();
            defaultList.add("الكل");
            insideCategoryAdapter.updateCategories(defaultList);
            selectedInsideCategoryPosition = 0;
            insideCategoryAdapter.setSelectedPosition(0);

            // تحميل المنتجات
            loadProductsForSelectedCategories();
            return;
        }

        int secondCategoryId = secondCategoriesList.get(secondCategoryPosition).id;

        // استخدام ViewModel المناسب لتحميل التصنيفات الداخلية
        // ملاحظة: هذا يتطلب وجود الدالة المناسبة في CategorisInsideViewModel
        // insideCategoryViewModel.setCurrentSecondCategoryIdForCard3(secondCategoryId);
        // insideCategoryViewModel.getCategoriesBySecondCategoryForCard3().observe(...);

        // حالياً نستخدم البيانات من المنتجات كحل مؤقت
        insideCategoriesList.clear();
        List<String> insideCategoryNames = new ArrayList<>();

        for (table_product product : allProducts) {
            if (product.getSecondCategoryId() == secondCategoryId && product.getInsideCategoryId() > 0) {
                // هنا يجب جلب اسم التصنيف الداخلي من قاعدة البيانات
                // حالياً نستخدم "قسم " + id كبديل
                String insideName = "قسم " + product.getInsideCategoryId();
                if (!insideCategoryNames.contains(insideName)) {
                    insideCategoryNames.add(insideName);
                }
            }
        }

        if (insideCategoryNames.isEmpty()) {
            insideCategoryNames.add("الكل");
        }

        insideCategoryAdapter.updateCategories(insideCategoryNames);
        selectedInsideCategoryPosition = 0;
        insideCategoryAdapter.setSelectedPosition(0);

        // تحميل المنتجات
        loadProductsForSelectedCategories();
    }

    private void loadProductsForSelectedCategories() {
        List<table_product> filteredProducts = new ArrayList<>();

        int selectedSecondCategoryId = 0;
        if (!secondCategoriesList.isEmpty() && selectedSecondCategoryPosition < secondCategoriesList.size()) {
            selectedSecondCategoryId = secondCategoriesList.get(selectedSecondCategoryPosition).id;
        }

        // فلترة المنتجات
        for (table_product product : allProducts) {
            // فلترة حسب التصنيف الفرعي
            if (selectedSecondCategoryId == 0 || product.getSecondCategoryId() == selectedSecondCategoryId) {
                filteredProducts.add(product);
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