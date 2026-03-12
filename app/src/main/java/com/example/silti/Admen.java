package com.example.silti;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;

import com.example.silti.databinding.ActivityAdmenBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class Admen extends AppCompatActivity {


    private ActivityAdmenBinding binding;


    // ViewModels
    private FirstCategoryViewModel firstCategoryViewModel;
    private SecondCategoryViewModel secondCategoryViewModel;
    private CategorisInsideViewModel insideCategoryViewModel;
    private ProductViewModel productViewModel;

    // Selected items
    private table_firstCategory selectedFirstCategory;
    private table_secondCategory selectedSecondCategory;
    private table_CategorisInside selectedInsideCategory;

    // Lists for spinners
    private List<table_firstCategory> firstCategoryList = new ArrayList<>();
    private List<table_secondCategory> secondCategoryList = new ArrayList<>();
    private List<table_CategorisInside> insideCategoryList = new ArrayList<>();

    // Image selection
    private Uri selectedImageUri;
    private String currentImagePath = "";

    // Image picker launcher
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    currentImagePath = uri.toString();
                    binding.ImgProduct.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdmenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModels();
        checkAdminAccess();
        setupObservers();
        setupClickListeners();
    }

    private void initViewModels() {
        firstCategoryViewModel = new ViewModelProvider(this).get(FirstCategoryViewModel.class);
        secondCategoryViewModel = new ViewModelProvider(this).get(SecondCategoryViewModel.class);
        insideCategoryViewModel = new ViewModelProvider(this).get(CategorisInsideViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
    }

    private void checkAdminAccess() {
        SharedPreferences prefs = getSharedPreferences("MyCartPrefs", MODE_PRIVATE);
        long currentAdminId = prefs.getLong("userId", -1);
        boolean isAdmin = prefs.getBoolean("isAdmin", false);

        if (!isAdmin || currentAdminId == -1) {
            Toast.makeText(this, "غير مصرح بالدخول", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        }
    }

    private void setupObservers() {
        // Observe First Categories
        firstCategoryViewModel.getAllActiveCategories().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                firstCategoryList.clear();
                firstCategoryList.addAll(categories);
                updateFirstCategorySpinners();
            }
        });

        // Observe Second Categories
        secondCategoryViewModel.getCategoriesByFirstCategory().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                secondCategoryList.clear();
                secondCategoryList.addAll(categories);
                updateSecondCategorySpinners();
            } else {
                secondCategoryList.clear();
                updateSecondCategorySpinners();
            }
        });

        // Observe Inside Categories
        insideCategoryViewModel.getCategoriesBySecondCategory().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                insideCategoryList.clear();
                insideCategoryList.addAll(categories);
                updateInsideCategorySpinners();
            } else {
                insideCategoryList.clear();
                updateInsideCategorySpinners();
            }
        });
    }

    private void setupClickListeners() {
        // Logout
        binding.logout.setOnClickListener(v -> showLogoutDialog());

        // Toggle cards
        binding.more1.setOnClickListener(v -> toggleCard(binding.card1));
        binding.more2.setOnClickListener(v -> toggleCard(binding.card2));
        binding.more3.setOnClickListener(v -> toggleCard(binding.card3));
        binding.more4.setOnClickListener(v -> toggleCard(binding.card4));

        // Image selection
        binding.ImgProduct.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // Add buttons
        binding.addCategorySecond.setOnClickListener(v -> addSecondCategory());
        binding.addCategoryIn.setOnClickListener(v -> addInsideCategory());
        binding.addProduct.setOnClickListener(v -> addProduct());
        binding.addCategory.setOnClickListener(v -> addDiscount());

        // First Category spinners
        binding.spinerFirstCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < firstCategoryList.size()) {
                    selectedFirstCategory = firstCategoryList.get(position);
                    if (selectedFirstCategory != null) {
                        secondCategoryViewModel.setCurrentFirstCategoryId(selectedFirstCategory.id);
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedFirstCategory = null;
            }
        });

        // Second Category spinners
        binding.spinerCategorySecond.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < secondCategoryList.size()) {
                    selectedSecondCategory = secondCategoryList.get(position);
                    if (selectedSecondCategory != null) {
                        insideCategoryViewModel.setCurrentSecondCategoryId(selectedSecondCategory.id);
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedSecondCategory = null;
            }
        });

        // Inside Category spinners
        binding.spinerCategoryIn.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < insideCategoryList.size()) {
                    selectedInsideCategory = insideCategoryList.get(position);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedInsideCategory = null;
            }
        });

        // Product First Category spinners
        binding.spinerFirstForProduct.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < firstCategoryList.size()) {
                    selectedFirstCategory = firstCategoryList.get(position);
                    if (selectedFirstCategory != null) {
                        secondCategoryViewModel.setCurrentFirstCategoryId(selectedFirstCategory.id);
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedFirstCategory = null;
            }
        });

        // Product Second Category spinners
        binding.spinerSecondForCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < secondCategoryList.size()) {
                    selectedSecondCategory = secondCategoryList.get(position);
                    if (selectedSecondCategory != null) {
                        insideCategoryViewModel.setCurrentSecondCategoryId(selectedSecondCategory.id);
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedSecondCategory = null;
            }
        });
    }

    private void updateFirstCategorySpinners() {
        if (firstCategoryList.isEmpty()) return;

        ArrayAdapter<table_firstCategory> adapter1 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, firstCategoryList);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerFirstCategory.setAdapter(adapter1);

        ArrayAdapter<table_firstCategory> adapter2 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, firstCategoryList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerFirstForProduct.setAdapter(adapter2);
    }

    private void updateSecondCategorySpinners() {
        ArrayAdapter<table_secondCategory> adapter1;
        if (secondCategoryList.isEmpty()) {
            adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                    new ArrayList<table_secondCategory>());
        } else {
            adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, secondCategoryList);
        }
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerCategorySecond.setAdapter(adapter1);

        ArrayAdapter<table_secondCategory> adapter2;
        if (secondCategoryList.isEmpty()) {
            adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                    new ArrayList<table_secondCategory>());
        } else {
            adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, secondCategoryList);
        }
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerSecondForCategory.setAdapter(adapter2);
    }

    private void updateInsideCategorySpinners() {
        ArrayAdapter<table_CategorisInside> adapter;
        if (insideCategoryList.isEmpty()) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                    new ArrayList<table_CategorisInside>());
        } else {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, insideCategoryList);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerCategoryIn.setAdapter(adapter);
    }

    private void toggleCard(View card) {
        card.setVisibility(card.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private void addSecondCategory() {
        String name = binding.nameCategorySecond.getText().toString().trim();

        if (name.isEmpty()) {
            showError("الرجاء إدخال اسم التصنيف");
            return;
        }

        if (selectedFirstCategory == null) {
            showError("الرجاء اختيار التصنيف الأساسي");
            return;
        }

        // Create and insert second category
        table_secondCategory category = new table_secondCategory(name, selectedFirstCategory.id, "");
        secondCategoryViewModel.insert(category);

        showSuccess("تم إضافة التصنيف الفرعي");
        binding.nameCategorySecond.setText("");
        binding.card1.setVisibility(View.GONE);
    }

    private void addInsideCategory() {
        String name = binding.nameCategoryIn.getText().toString().trim();

        if (name.isEmpty()) {
            showError("الرجاء إدخال الاسم");
            return;
        }

        if (selectedSecondCategory == null) {
            showError("الرجاء اختيار التصنيف الفرعي");
            return;
        }

        // Get max position and insert
        insideCategoryViewModel.getMaxPositionBySecondCategory(selectedSecondCategory.id,
                new CategorisInsideRepository.PositionCallback() {
                    @Override
                    public void onResult(int maxPosition) {
                        table_CategorisInside category = new table_CategorisInside(
                                selectedSecondCategory.id, name, "", maxPosition + 1);
                        insideCategoryViewModel.insert(category);

                        runOnUiThread(() -> {
                            showSuccess("تم إضافة التصنيف الداخلي");
                            binding.nameCategoryIn.setText("");
                            binding.card2.setVisibility(View.GONE);
                        });
                    }
                });
    }

    private void addProduct() {
        String name = binding.nameProduct.getText().toString().trim();
        String priceStr = binding.PriceProduct.getText().toString().trim();
        String rateStr = binding.rateProduct.getText().toString().trim();
        String description = binding.discribtion.getText().toString().trim();

        if (name.isEmpty()) {
            showError("الرجاء إدخال اسم المنتج");
            return;
        }

        if (priceStr.isEmpty()) {
            showError("الرجاء إدخال السعر");
            return;
        }

        if (selectedFirstCategory == null) {
            showError("الرجاء اختيار التصنيف الأساسي");
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
            product.setDiscount(0);
            product.setSoldCount(0);

            productViewModel.insertProduct(product);

            showSuccess("تم إضافة المنتج بنجاح");
            clearProductFields();
            binding.card3.setVisibility(View.GONE);

        } catch (NumberFormatException e) {
            showError("الرجاء إدخال أرقام صحيحة");
        }
    }

    private void addDiscount() {
        String productName = binding.nameproductForDis.getText().toString().trim();
        String oldPriceStr = binding.oldPrice.getText().toString().trim();
        String newPriceStr = binding.newPrice.getText().toString().trim();

        if (productName.isEmpty() || oldPriceStr.isEmpty() || newPriceStr.isEmpty()) {
            showError("الرجاء إدخال جميع البيانات");
            return;
        }

        try {
            double oldPrice = Double.parseDouble(oldPriceStr);
            double newPrice = Double.parseDouble(newPriceStr);

            if (newPrice >= oldPrice) {
                showError("السعر الجديد يجب أن يكون أقل من القديم");
                return;
            }

            double discount = ((oldPrice - newPrice) / oldPrice) * 100;

            if (discount < 0 || discount > 100) {
                showError("نسبة الخصم غير صحيحة");
                return;
            }

            final double discountFinal = discount;

            // Search for product
            productViewModel.searchProducts(productName).observe(this, products -> {
                if (products != null && !products.isEmpty()) {
                    table_product product = products.get(0);
                    product.setDiscount(discountFinal);
                    product.setPrice(newPrice);

                    productViewModel.updateProduct(product);

                    showSuccess("تم إضافة الخصم بنجاح");
                    binding.nameproductForDis.setText("");
                    binding.oldPrice.setText("");
                    binding.newPrice.setText("");
                    binding.card4.setVisibility(View.GONE);
                } else {
                    showError("المنتج غير موجود");
                }
            });

        } catch (NumberFormatException e) {
            showError("الرجاء إدخال أرقام صحيحة");
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

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getColor(R.color.red))
                .setTextColor(getColor(R.color.white))
                .show();
    }

    private void showSuccess(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getColor(R.color.primary))
                .setTextColor(getColor(R.color.white))
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("تسجيل الخروج")
                .setMessage("هل أنت متأكد من تسجيل الخروج؟")
                .setPositiveButton("نعم", (dialog, which) -> {
                    SharedPreferences prefs = getSharedPreferences("MyCartPrefs", MODE_PRIVATE);
                    prefs.edit().clear().apply();
                    navigateToLogin();
                })
                .setNegativeButton("لا", null)
                .show();
    }

    private void navigateToLogin() {
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