package com.example.silti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.silti.databinding.ActivityAdmenBinding;
import com.example.silti.databinding.ActivityLoginBinding;

public class login extends AppCompatActivity {

    private ActivityAdmenBinding binding;

    // ViewModels
    private FirstCategoryViewModel firstCategoryViewModel;
    private SecondCategoryViewModel secondCategoryViewModel;
    private CategorisInsideViewModel insideCategoryViewModel;
    private ProductViewModel productViewModel;
    private SizeViewModel sizeViewModel;

    // Adapters
    private CategoryAdapter firstCategoryAdapter;
    private CategoryAdapter secondCategoryAdapter;
    private CategoryAdapter insideCategoryAdapter;
    private ProductAdapter productAdapter;
    private DiscountAdapter discountAdapter;

    // Selected items
    private table_firstCategory selectedFirstCategory;
    private table_secondCategory selectedSecondCategory;
    private table_CategorisInside selectedInsideCategory;
    private table_product selectedProduct;

    // Image selection
    private Uri selectedImageUri;
    private String currentImagePath = "";

    // ActivityResultLauncher for image picker
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContract<String, Object>() {
            }.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    currentImagePath = uri.toString();
                    // عرض الصورة المحددة
                    if (binding.card3.getVisibility() == View.VISIBLE) {
                        binding.ImgProduct.setImageURI(uri);
                    } else if (binding.card4.getVisibility() == View.VISIBLE) {
                        // للخصومات
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdmenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModels();
        setupClickListeners();
        setupTabs();
        loadData();
    }

    private void initViewModels() {
        firstCategoryViewModel = new ViewModelProvider(this).get(FirstCategoryViewModel.class);
        secondCategoryViewModel = new ViewModelProvider(this).get(SecondCategoryViewModel.class);
        insideCategoryViewModel = new ViewModelProvider(this).get(CategorisInsideViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        sizeViewModel = new ViewModelProvider(this).get(SizeViewModel.class);
    }

    private void setupTabs() {
        // يمكن إضافة تبويبات للتنقل بين الأقسام
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("التصنيفات"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("المنتجات"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("الخصومات"));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        showCategoriesSection();
                        break;
                    case 1:
                        showProductsSection();
                        break;
                    case 2:
                        showDiscountsSection();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showCategoriesSection() {
        binding.scroll.setVisibility(View.VISIBLE);
        // إظهار أقسام التصنيفات
    }

    private void showProductsSection() {
        binding.scroll.setVisibility(View.GONE);
        // إظهار قائمة المنتجات
    }

    private void showDiscountsSection() {
        binding.scroll.setVisibility(View.GONE);
        // إظهار قائمة الخصومات
    }

    private void setupClickListeners() {
        // Logout
        binding.logout.setOnClickListener(v -> logout());

        // Toggle cards
        binding.more1.setOnClickListener(v -> toggleCard(binding.card1));
        binding.more2.setOnClickListener(v -> toggleCard(binding.card2));
        binding.more3.setOnClickListener(v -> toggleCard(binding.card3));
        binding.more4.setOnClickListener(v -> toggleCard(binding.card4));

        // Image selection for product
        binding.ImgProduct.setOnClickListener(v ->
                imagePickerLauncher.launch("image/*"));

        // Add buttons
        binding.addCategorySecond.setOnClickListener(v -> addSecondCategory());
        binding.addCategoryIn.setOnClickListener(v -> addInsideCategory());
        binding.addProduct.setOnClickListener(v -> addProduct());
        binding.addCategory.setOnClickListener(v -> addDiscount());

        // Category spinners selection
        binding.spinerFirstCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedFirstCategory = (table_firstCategory) parent.getItemAtPosition(position);
                if (selectedFirstCategory != null) {
                    secondCategoryViewModel.setCurrentFirstCategoryId(selectedFirstCategory.id);
                    loadSecondCategoriesForSpinner();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        binding.spinerCategorySecond.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedSecondCategory = (table_secondCategory) parent.getItemAtPosition(position);
                if (selectedSecondCategory != null) {
                    insideCategoryViewModel.setCurrentSecondCategoryId(selectedSecondCategory.id);
                    loadInsideCategoriesForSpinner();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Product category spinners
        binding.spinerFirstForProduct.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedFirstCategory = (table_firstCategory) parent.getItemAtPosition(position);
                if (selectedFirstCategory != null) {
                    loadSecondCategoriesForProductSpinner(selectedFirstCategory.id);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        binding.spinerSecondForCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedSecondCategory = (table_secondCategory) parent.getItemAtPosition(position);
                if (selectedSecondCategory != null) {
                    loadInsideCategoriesForProductSpinner(selectedSecondCategory.id);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void toggleCard(View card) {
        card.setVisibility(card.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private void loadData() {
        // Load First Categories
        firstCategoryViewModel.getAllActiveCategories().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                updateFirstCategorySpinners(categories);
            }
        });

        // Load Sizes
        sizeViewModel.getAllSizes().observe(this, sizes -> {
            if (sizes != null) {
                // يمكن عرض المقاسات في CheckBoxes
            }
        });

        // Load Products for management
        productViewModel.getAllProducts().observe(this, products -> {
            if (products != null) {
                // تحديث قائمة المنتجات
            }
        });
    }

    private void updateFirstCategorySpinners(List<table_firstCategory> categories) {
        ArrayAdapter<table_firstCategory> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinerFirstCategory.setAdapter(adapter);
        binding.spinerFirstForProduct.setAdapter(adapter);
    }

    private void loadSecondCategoriesForSpinner() {
        secondCategoryViewModel.getCategoriesByFirstCategory().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                ArrayAdapter<table_secondCategory> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinerCategorySecond.setAdapter(adapter);
            }
        });
    }

    private void loadSecondCategoriesForProductSpinner(int firstCategoryId) {
        secondCategoryViewModel.getCategoriesByFirstCategory(firstCategoryId).observe(this, categories -> {
            if (categories != null) {
                ArrayAdapter<table_secondCategory> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

    private void loadInsideCategoriesForProductSpinner(int secondCategoryId) {
        insideCategoryViewModel.getCategoriesBySecondCategory(secondCategoryId).observe(this, categories -> {
            if (categories != null) {
                ArrayAdapter<table_CategorisInside> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinerCategoryIn.setAdapter(adapter);
            }
        });
    }

    private void addSecondCategory() {
        String name = binding.nameCategorySecond.getText().toString().trim();

        if (name.isEmpty() || selectedFirstCategory == null) {
            Toast.makeText(this, "الرجاء إدخال الاسم واختيار التصنيف", Toast.LENGTH_SHORT).show();
            return;
        }

        // استخدام insert(String name, int firstCategoryId, String icon)
        secondCategoryViewModel.insert(name, selectedFirstCategory.id, selectedFirstCategory.icon);

        Toast.makeText(this, "تم إضافة التصنيف الفرعي", Toast.LENGTH_SHORT).show();
        binding.nameCategorySecond.setText("");
        binding.card1.setVisibility(View.GONE);
    }

    private void addInsideCategory() {
        String name = binding.nameCategoryIn.getText().toString().trim();

        if (name.isEmpty() || selectedSecondCategory == null) {
            Toast.makeText(this, "الرجاء إدخال الاسم واختيار التصنيف", Toast.LENGTH_SHORT).show();
            return;
        }

        // الحصول على أقصى ترتيب
        insideCategoryViewModel.getMaxPositionBySecondCategory(selectedSecondCategory.id,
                new CategorisInsideRepository.PositionCallback() {
                    @Override
                    public void onResult(int maxPosition) {
                        // insert with auto position
                        insideCategoryViewModel.insert(selectedSecondCategory.id, name, "", maxPosition + 1);
                    }
                });

        Toast.makeText(this, "تم إضافة التصنيف الداخلي", Toast.LENGTH_SHORT).show();
        binding.nameCategoryIn.setText("");
        binding.card2.setVisibility(View.GONE);
    }

    private void addProduct() {
        String name = binding.nameProduct.getText().toString().trim();
        String priceStr = binding.PriceProduct.getText().toString().trim();
        String rateStr = binding.rateProduct.getText().toString().trim();
        String description = binding.discribtion.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || selectedFirstCategory == null) {
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
            product.setFirstCategoryId(selectedFirstCategory.id);
            product.setSecondCategoryId(selectedSecondCategory != null ? selectedSecondCategory.id : 0);
            product.setInsideCategoryId(selectedInsideCategory != null ? selectedInsideCategory.id : 0);
            product.setActive(true);
            product.setFeatured(false);
            product.setQuantity(100);
            product.setImage(currentImagePath);

            // جمع المقاسات المحددة
            List<String> selectedSizes = new ArrayList<>();
            if (binding.small.isChecked()) selectedSizes.add("S");
            if (binding.Larg.isChecked()) selectedSizes.add("L");
            if (binding.XLarg.isChecked()) selectedSizes.add("XL");
            if (binding.XXLarg.isChecked()) selectedSizes.add("XXL");
            if (binding.XXXLarg.isChecked()) selectedSizes.add("XXXL");

            // إضافة المنتج
            productViewModel.insertProduct(product);

            Toast.makeText(this, "تم إضافة المنتج بنجاح", Toast.LENGTH_SHORT).show();
            clearProductFields();
            binding.card3.setVisibility(View.GONE);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "الرجاء إدخال أرقام صحيحة", Toast.LENGTH_SHORT).show();
        }
    }

    private void addDiscount() {
        String productName = binding.nameproductForDis.getText().toString().trim();
        String oldPriceStr = binding.oldPrice.getText().toString().trim();
        String newPriceStr = binding.newPrice.getText().toString().trim();

        if (productName.isEmpty() || oldPriceStr.isEmpty() || newPriceStr.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال جميع البيانات", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double oldPrice = Double.parseDouble(oldPriceStr);
            double newPrice = Double.parseDouble(newPriceStr);
            double discount = ((oldPrice - newPrice) / oldPrice) * 100;

            // البحث عن المنتج وإضافة الخصم
            productViewModel.searchProducts(productName).observe(this, products -> {
                if (products != null && !products.isEmpty()) {
                    table_product product = products.get(0);
                    product.setDiscount(discount);
                    product.setPrice(newPrice);
                    productViewModel.updateProduct(product);

                    Toast.makeText(this, "تم إضافة الخصم بنجاح", Toast.LENGTH_SHORT).show();
                    binding.nameproductForDis.setText("");
                    binding.oldPrice.setText("");
                    binding.newPrice.setText("");
                    binding.card4.setVisibility(View.GONE);
                } else {
                    Toast.makeText(this, "المنتج غير موجود", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "الرجاء إدخال أرقام صحيحة", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearProductFields() {
        binding.nameProduct.setText("");
        binding.PriceProduct.setText("");
        binding.rateProduct.setText("");
        binding.discribtion.setText("");

        binding.small.setChecked(false);
        binding.Larg.setChecked(false);
        binding.XLarg.setChecked(false);
        binding.XXLarg.setChecked(false);
        binding.XXXLarg.setChecked(false);

        binding.ImgProduct.setImageResource(R.drawable.add);
        currentImagePath = "";
        selectedImageUri = null;
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("تسجيل الخروج")
                .setMessage("هل أنت متأكد من تسجيل الخروج؟")
                .setPositiveButton("نعم", (dialog, which) -> {
                    SharedPreferences prefs = getSharedPreferences("MyCartPrefs", MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    Intent intent = new Intent(this, login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("لا", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}