package com.example.silti;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.silti.databinding.ActivityAdmenBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Admen extends AppCompatActivity {
    private ActivityAdmenBinding binding;

    // ViewModels
    private FirstCategoryViewModel firstCategoryViewModel;
    private SecondCategoryViewModel secondCategoryViewModel;
    private CategorisInsideViewModel insideCategoryViewModel;
    private ProductViewModel productViewModel;

    // Lists
    private List<table_firstCategory> firstCategoryList = new ArrayList<>();
    private List<table_secondCategory> secondCategoryList = new ArrayList<>();
    private List<table_CategorisInside> insideCategoryList = new ArrayList<>();
    private List<table_product> productList = new ArrayList<>();

    // Adapters
    private ArrayAdapter<String> firstCategoryAdapter1;
    private ArrayAdapter<String> firstCategoryAdapter2;
    private ArrayAdapter<String> secondCategoryAdapter1;
    private ArrayAdapter<String> secondCategoryAdapter2;
    private ArrayAdapter<String> insideCategoryAdapter;

    // Selected items
    private table_firstCategory selectedFirstCategory;
    private table_secondCategory selectedSecondCategory;
    private table_CategorisInside selectedInsideCategory;
    private table_product selectedProductForDiscount;

    // Image selection
    private Uri selectedImageUri;
    private String savedImagePath = "";

    // قائمة التصنيفات الأساسية الستة
    private final String[] DEFAULT_CATEGORIES = {
            "موضة", "جمال", "كتب", "بيت", "رياضة", "إلكترونيات"
    };

    // Image picker launcher
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    // عرض الصورة مؤقتاً
                    binding.ImgProduct.setImageURI(uri);
                    Toast.makeText(this, "تم اختيار الصورة", Toast.LENGTH_SHORT).show();
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
        setupTextWatchers();
        initializeDefaultCategories();
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

    private void initializeDefaultCategories() {
        firstCategoryViewModel.getAllActiveCategories().observe(this, categories -> {
            if (categories == null || categories.isEmpty()) {
                // إضافة التصنيفات الأساسية إذا لم تكن موجودة
                for (int i = 0; i < DEFAULT_CATEGORIES.length; i++) {
                    String categoryName = DEFAULT_CATEGORIES[i];
                    String icon = getCategoryIcon(categoryName);
                    table_firstCategory category = new table_firstCategory(
                            0, categoryName, icon, i + 1, true, System.currentTimeMillis()
                    );
                    firstCategoryViewModel.insert(category);
                }
            }
        });
    }

    private String getCategoryIcon(String categoryName) {
        switch (categoryName) {
            case "موضة": return "ic_fashion";
            case "جمال": return "ic_beauty";
            case "كتب": return "ic_books";
            case "بيت": return "ic_home";
            case "رياضة": return "ic_sports";
            case "إلكترونيات": return "ic_electronics";
            default: return "ic_category";
        }
    }

    private void setupObservers() {
        // مراقبة التصنيفات الأساسية
        firstCategoryViewModel.getAllActiveCategories().observe(this, categories -> {
            if (categories != null) {
                firstCategoryList.clear();
                firstCategoryList.addAll(categories);
                updateFirstCategorySpinners();
            }
        });

        // مراقبة جميع المنتجات
        productViewModel.getAllProducts().observe(this, products -> {
            if (products != null) {
                productList.clear();
                productList.addAll(products);
            }
        });
    }

    private void observeSecondCategories() {
        secondCategoryViewModel.getCategoriesByFirstCategory().observe(this, categories -> {
            if (categories != null) {
                secondCategoryList.clear();
                secondCategoryList.addAll(categories);
                updateSecondCategorySpinners();
            }
        });
    }

    private void observeInsideCategories() {
        insideCategoryViewModel.getCategoriesBySecondCategory().observe(this, categories -> {
            if (categories != null) {
                insideCategoryList.clear();
                insideCategoryList.addAll(categories);
                updateInsideCategorySpinners();
            }
        });
    }

    private void setupClickListeners() {
        // تسجيل الخروج
        binding.logout.setOnClickListener(v -> showLogoutDialog());

        // أزرار إظهار/إخفاء البطاقات
        binding.more1.setOnClickListener(v -> toggleCard(binding.card1));
        binding.more2.setOnClickListener(v -> toggleCard(binding.card2));
        binding.more3.setOnClickListener(v -> toggleCard(binding.card3));
        binding.more4.setOnClickListener(v -> toggleCard(binding.card4));

        // اختيار الصورة - تعديل لفتح معرض الصور
        binding.ImgProduct.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // أزرار الإضافة
        binding.addCategorySecond.setOnClickListener(v -> addSecondCategory());
        binding.addCategoryIn.setOnClickListener(v -> addInsideCategory());
        binding.addProduct.setOnClickListener(v -> addProduct());
        binding.addCategory.setOnClickListener(v -> addDiscount());

        // ========== البطاقة الأولى: إضافة تصنيف فرعي ==========
        binding.spinerFirstCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < firstCategoryList.size()) {
                    selectedFirstCategory = firstCategoryList.get(position);
                    binding.CategoryTextChoose.setText(selectedFirstCategory.name);
                } else {
                    selectedFirstCategory = null;
                    binding.CategoryTextChoose.setText("التصنيف");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFirstCategory = null;
            }
        });

        // ========== البطاقة الثانية: إضافة تصنيف داخلي ==========
        binding.spinerCategorySecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < secondCategoryList.size()) {
                    selectedSecondCategory = secondCategoryList.get(position);
                    binding.CategoryTextChooseIn.setText(selectedSecondCategory.name);
                } else {
                    selectedSecondCategory = null;
                    binding.CategoryTextChooseIn.setText("التصنيف");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSecondCategory = null;
            }
        });

        // ========== البطاقة الثالثة: إضافة منتج ==========
        // Spinner التصنيف الأساسي
        binding.spinerFirstForProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < firstCategoryList.size()) {
                    selectedFirstCategory = firstCategoryList.get(position);
                    binding.TextChooseFirst.setText(selectedFirstCategory.name);

                    if (selectedFirstCategory != null) {
                        secondCategoryViewModel.setCurrentFirstCategoryId(selectedFirstCategory.id);
                        observeSecondCategories();
                    }
                } else {
                    selectedFirstCategory = null;
                    binding.TextChooseFirst.setText("التصنيف الاساسي");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFirstCategory = null;
            }
        });

        // Spinner التصنيف الفرعي
        binding.spinerSecondForCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < secondCategoryList.size()) {
                    selectedSecondCategory = secondCategoryList.get(position);
                    binding.TextForSecond.setText(selectedSecondCategory.name);

                    if (selectedSecondCategory != null) {
                        insideCategoryViewModel.setCurrentSecondCategoryId(selectedSecondCategory.id);
                        observeInsideCategories();
                    }
                } else {
                    selectedSecondCategory = null;
                    binding.TextForSecond.setText("التصنيف الفرعي");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSecondCategory = null;
            }
        });

        // Spinner التصنيف الداخلي
        binding.spinerCategoryIn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < insideCategoryList.size()) {
                    selectedInsideCategory = insideCategoryList.get(position);
                    binding.CategoryTextIn.setText(selectedInsideCategory.name);
                } else {
                    selectedInsideCategory = null;
                    binding.CategoryTextIn.setText("التصنيف الداخلي");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedInsideCategory = null;
            }
        });
    }

    private void setupTextWatchers() {
        // ========== البطاقة الرابعة: البحث عن المنتج تلقائياً ==========
        binding.nameproductForDis.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    searchProduct(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void searchProduct(String query) {
        productViewModel.searchProducts(query).observe(this, products -> {
            if (products != null && !products.isEmpty()) {
                selectedProductForDiscount = products.get(0);
                binding.oldPrice.setText(String.valueOf((int)selectedProductForDiscount.getPrice()));
            } else {
                selectedProductForDiscount = null;
                binding.oldPrice.setText("");
            }
        });
    }

    private void updateFirstCategorySpinners() {
        if (firstCategoryList.isEmpty()) return;

        List<String> categoryNames = new ArrayList<>();
        for (table_firstCategory category : firstCategoryList) {
            categoryNames.add(category.name);
        }

        firstCategoryAdapter1 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryNames);
        firstCategoryAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerFirstCategory.setAdapter(firstCategoryAdapter1);

        firstCategoryAdapter2 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryNames);
        firstCategoryAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerFirstForProduct.setAdapter(firstCategoryAdapter2);
    }

    private void updateSecondCategorySpinners() {
        List<String> categoryNames = new ArrayList<>();
        for (table_secondCategory category : secondCategoryList) {
            categoryNames.add(category.name);
        }

        secondCategoryAdapter1 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryNames);
        secondCategoryAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerCategorySecond.setAdapter(secondCategoryAdapter1);

        secondCategoryAdapter2 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryNames);
        secondCategoryAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerSecondForCategory.setAdapter(secondCategoryAdapter2);
    }

    private void updateInsideCategorySpinners() {
        List<String> categoryNames = new ArrayList<>();
        for (table_CategorisInside category : insideCategoryList) {
            categoryNames.add(category.name);
        }

        insideCategoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryNames);
        insideCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerCategoryIn.setAdapter(insideCategoryAdapter);

        // إذا كانت القائمة فارغة، نعرض رسالة
        if (insideCategoryList.isEmpty()) {
            List<String> emptyList = new ArrayList<>();
            emptyList.add("لا توجد تصنيفات داخلية");
            ArrayAdapter<String> tempAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, emptyList);
            tempAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinerCategoryIn.setAdapter(tempAdapter);
        }
    }

    private void toggleCard(View card) {
        card.setVisibility(card.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    // ========== البطاقة الأولى: إضافة تصنيف فرعي ==========
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

        table_secondCategory category = new table_secondCategory(name, selectedFirstCategory.id, "");
        secondCategoryViewModel.insert(category);

        secondCategoryViewModel.setCurrentFirstCategoryId(selectedFirstCategory.id);
        observeSecondCategories();

        showSuccess("تم إضافة التصنيف الفرعي: " + name + " إلى قسم " + selectedFirstCategory.name);
        binding.nameCategorySecond.setText("");
        binding.card1.setVisibility(View.GONE);
    }

    // ========== البطاقة الثانية: إضافة تصنيف داخلي ==========
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

        final int secondCategoryId = selectedSecondCategory.id;
        final String secondCategoryName = selectedSecondCategory.name;

        // تعطيل الزر لمنع النقر المتكرر
        binding.addCategoryIn.setEnabled(false);
        binding.addCategoryIn.setText("جاري الإضافة...");

        insideCategoryViewModel.getMaxPositionBySecondCategory(secondCategoryId,
                new CategorisInsideRepository.PositionCallback() {
                    @Override
                    public void onResult(int maxPosition) {
                        table_CategorisInside category = new table_CategorisInside(
                                secondCategoryId, name, "", maxPosition + 1);
                        insideCategoryViewModel.insert(category);

                        runOnUiThread(() -> {
                            // إعادة تفعيل الزر
                            binding.addCategoryIn.setEnabled(true);
                            binding.addCategoryIn.setText("إضافة");

                            // تحديث القائمة والـ Spinner
                            insideCategoryViewModel.setCurrentSecondCategoryId(secondCategoryId);
                            observeInsideCategories();

                            showSuccess("تم إضافة التصنيف الداخلي: " + name +
                                    " إلى قسم " + secondCategoryName);
                            binding.nameCategoryIn.setText("");
                            binding.card2.setVisibility(View.GONE);
                        });
                    }
                });
    }

    // ========== البطاقة الثالثة: إضافة منتج مع حفظ الصورة ==========
    private void addProduct() {
        String name = binding.nameProduct.getText().toString().trim();
        String priceStr = binding.PriceProduct.getText().toString().trim();
        String rateStr = binding.rateProduct.getText().toString().trim();
        String description = binding.discribtion.getText().toString().trim();
        String customSizes = binding.enterSizes.getText().toString().trim();

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

            // ✅ حفظ الصورة إذا تم اختيارها
            String imagePathToSave = "";
            if (selectedImageUri != null) {
                imagePathToSave = saveImageToInternalStorage(selectedImageUri);
                if (imagePathToSave.isEmpty()) {
                    showError("فشل في حفظ الصورة");
                    return;
                }
            }

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
            product.setImage(imagePathToSave); // حفظ المسار الحقيقي للصورة
            product.setDiscount(0);
            product.setSoldCount(0);

            productViewModel.insertProduct(product);

            String categoryPath = selectedFirstCategory.name;
            if (selectedSecondCategory != null) {
                categoryPath += " → " + selectedSecondCategory.name;
            }
            if (selectedInsideCategory != null) {
                categoryPath += " → " + selectedInsideCategory.name;
            }

            showSuccess("تم إضافة المنتج: " + name + " إلى " + categoryPath);
            clearProductFields();
            binding.card3.setVisibility(View.GONE);

        } catch (NumberFormatException e) {
            showError("الرجاء إدخال أرقام صحيحة");
        }
    }

    // ✅ دالة جديدة لحفظ الصورة في التخزين الداخلي للتطبيق
    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            // إنشاء اسم ملف فريد
            String fileName = "product_" + System.currentTimeMillis() + ".jpg";

            // فتح مجلد الصور الخاص بالتطبيق
            File imagesDir = new File(getFilesDir(), "product_images");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }

            // إنشاء الملف الكامل
            File imageFile = new File(imagesDir, fileName);

            // نسخ محتوى الصورة
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(imageFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            // إرجاع المسار النسبي للتخزين في قاعدة البيانات
            String savedPath = imageFile.getAbsolutePath();
            Log.d("Admen", "تم حفظ الصورة في: " + savedPath);

            return savedPath;

        } catch (Exception e) {
            e.printStackTrace();
            showError("خطأ في حفظ الصورة: " + e.getMessage());
            return "";
        }
    }

    // ========== البطاقة الرابعة: إضافة خصم ==========
    private void addDiscount() {
        String newPriceStr = binding.newPrice.getText().toString().trim();

        if (selectedProductForDiscount == null) {
            showError("الرجاء إدخال اسم منتج صحيح");
            return;
        }

        if (newPriceStr.isEmpty()) {
            showError("الرجاء إدخال السعر الجديد");
            return;
        }

        try {
            double oldPrice = selectedProductForDiscount.getPrice();
            double newPrice = Double.parseDouble(newPriceStr);

            if (newPrice >= oldPrice) {
                showError("السعر الجديد يجب أن يكون أقل من القديم");
                return;
            }

            double discount = ((oldPrice - newPrice) / oldPrice) * 100;

            selectedProductForDiscount.setDiscount(discount);
            selectedProductForDiscount.setPrice(newPrice);

            productViewModel.updateProduct(selectedProductForDiscount);

            showSuccess("تم إضافة خصم " + String.format("%.0f", discount) +
                    "% على المنتج: " + selectedProductForDiscount.getName());

            binding.nameproductForDis.setText("");
            binding.oldPrice.setText("");
            binding.newPrice.setText("");
            binding.card4.setVisibility(View.GONE);
            selectedProductForDiscount = null;

        } catch (NumberFormatException e) {
            showError("الرجاء إدخال أرقام صحيحة");
        }
    }

    private void clearProductFields() {
        binding.nameProduct.setText("");
        binding.PriceProduct.setText("");
        binding.rateProduct.setText("");
        binding.discribtion.setText("");
        binding.enterSizes.setText("");

        binding.small.setChecked(false);
        binding.Larg.setChecked(false);
        binding.XLarg.setChecked(false);
        binding.XXLarg.setChecked(false);
        binding.XXXLarg.setChecked(false);

        binding.ImgProduct.setImageResource(R.drawable.add);
        selectedImageUri = null;
        savedImagePath = "";
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getColor(android.R.color.holo_red_dark))
                .setTextColor(getColor(android.R.color.white))
                .show();
    }

    private void showSuccess(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getColor(R.color.primary))
                .setTextColor(getColor(android.R.color.white))
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
