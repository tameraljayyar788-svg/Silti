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
    private List<table_secondCategory> secondCategoryListForCard2 = new ArrayList<>(); // للبطاقة الثانية
    private List<table_secondCategory> secondCategoryListForCard3 = new ArrayList<>(); // للبطاقة الثالثة
    private List<table_CategorisInside> insideCategoryListForCard3 = new ArrayList<>(); // للبطاقة الثالثة
    private List<table_product> productList = new ArrayList<>();

    // Adapters
    private ArrayAdapter<String> firstCategoryAdapter1; // للبطاقة الأولى
    private ArrayAdapter<String> firstCategoryAdapter2; // للبطاقة الثانية
    private ArrayAdapter<String> firstCategoryAdapter3; // للبطاقة الثالثة
    private ArrayAdapter<String> secondCategoryAdapter2; // للبطاقة الثانية
    private ArrayAdapter<String> secondCategoryAdapter3; // للبطاقة الثالثة
    private ArrayAdapter<String> insideCategoryAdapter3; // للبطاقة الثالثة

    // Selected items - لكل بطاقة متغيرات منفصلة
    private table_firstCategory selectedFirstCategoryForCard1; // للبطاقة الأولى
    private table_firstCategory selectedFirstCategoryForCard2; // للبطاقة الثانية
    private table_firstCategory selectedFirstCategoryForCard3; // للبطاقة الثالثة

    private table_secondCategory selectedSecondCategoryForCard2; // للبطاقة الثانية
    private table_secondCategory selectedSecondCategoryForCard3; // للبطاقة الثالثة
    private table_CategorisInside selectedInsideCategoryForCard3; // للبطاقة الثالثة

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
        // مراقبة التصنيفات الأساسية (لجميع البطاقات)
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

    private void loadSecondCategoriesForCard2(int firstCategoryId) {
        secondCategoryViewModel.setCurrentFirstCategoryIdForCard2(firstCategoryId);
        secondCategoryViewModel.getCategoriesByFirstCategoryForCard2().observe(this, categories -> {
            if (categories != null) {
                secondCategoryListForCard2.clear();
                secondCategoryListForCard2.addAll(categories);
                updateSecondCategorySpinnersForCard2();
            }
        });
    }

    private void loadSecondCategoriesForCard3(int firstCategoryId) {
        secondCategoryViewModel.setCurrentFirstCategoryIdForCard3(firstCategoryId);
        secondCategoryViewModel.getCategoriesByFirstCategoryForCard3().observe(this, categories -> {
            if (categories != null) {
                secondCategoryListForCard3.clear();
                secondCategoryListForCard3.addAll(categories);
                updateSecondCategorySpinnersForCard3();
            }
        });
    }

    private void loadInsideCategoriesForCard3(int secondCategoryId) {
        insideCategoryViewModel.setCurrentSecondCategoryIdForCard3(secondCategoryId);
        insideCategoryViewModel.getCategoriesBySecondCategoryForCard3().observe(this, categories -> {
            if (categories != null) {
                insideCategoryListForCard3.clear();
                insideCategoryListForCard3.addAll(categories);
                updateInsideCategorySpinnersForCard3();
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

        // اختيار الصورة
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
                    selectedFirstCategoryForCard1 = firstCategoryList.get(position);
                    binding.CategoryTextChoose.setText(selectedFirstCategoryForCard1.name);
                } else {
                    selectedFirstCategoryForCard1 = null;
                    binding.CategoryTextChoose.setText("التصنيف");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFirstCategoryForCard1 = null;
            }
        });

        // ========== البطاقة الثانية: إضافة تصنيف داخلي ==========
        // Spinner التصنيف الأساسي في البطاقة الثانية
        binding.spinerCategoryFirst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < firstCategoryList.size()) {
                    selectedFirstCategoryForCard2 = firstCategoryList.get(position);
                    binding.CategoryTextChooseIn.setText(selectedFirstCategoryForCard2.name);

                    // تحميل التصنيفات الفرعية لهذا التصنيف الأساسي
                    if (selectedFirstCategoryForCard2 != null) {
                        loadSecondCategoriesForCard2(selectedFirstCategoryForCard2.id);
                    }
                } else {
                    selectedFirstCategoryForCard2 = null;
                    binding.CategoryTextChooseIn.setText("التصنيف الاساسي");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFirstCategoryForCard2 = null;
            }
        });

        // Spinner التصنيف الفرعي في البطاقة الثانية
        binding.spinerCategorySecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < secondCategoryListForCard2.size()) {
                    selectedSecondCategoryForCard2 = secondCategoryListForCard2.get(position);
                    binding.CategoryTextChooseSecond.setText(selectedSecondCategoryForCard2.name);
                } else {
                    selectedSecondCategoryForCard2 = null;
                    binding.CategoryTextChooseSecond.setText("التصنيف الفرعي");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSecondCategoryForCard2 = null;
            }
        });

        // ========== البطاقة الثالثة: إضافة منتج ==========
        // Spinner التصنيف الأساسي
        binding.spinerFirstForProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < firstCategoryList.size()) {
                    selectedFirstCategoryForCard3 = firstCategoryList.get(position);
                    binding.TextChooseFirst.setText(selectedFirstCategoryForCard3.name);

                    if (selectedFirstCategoryForCard3 != null) {
                        loadSecondCategoriesForCard3(selectedFirstCategoryForCard3.id);
                    }
                } else {
                    selectedFirstCategoryForCard3 = null;
                    binding.TextChooseFirst.setText("التصنيف الاساسي");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFirstCategoryForCard3 = null;
            }
        });

        // Spinner التصنيف الفرعي
        binding.spinerSecondForCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < secondCategoryListForCard3.size()) {
                    selectedSecondCategoryForCard3 = secondCategoryListForCard3.get(position);
                    binding.TextForSecond.setText(selectedSecondCategoryForCard3.name);

                    if (selectedSecondCategoryForCard3 != null) {
                        loadInsideCategoriesForCard3(selectedSecondCategoryForCard3.id);
                    }
                } else {
                    selectedSecondCategoryForCard3 = null;
                    binding.TextForSecond.setText("التصنيف الفرعي");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSecondCategoryForCard3 = null;
            }
        });

        // Spinner التصنيف الداخلي
        binding.spinerCategoryIn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < insideCategoryListForCard3.size()) {
                    selectedInsideCategoryForCard3 = insideCategoryListForCard3.get(position);
                    binding.CategoryTextIn.setText(selectedInsideCategoryForCard3.name);
                } else {
                    selectedInsideCategoryForCard3 = null;
                    binding.CategoryTextIn.setText("التصنيف الداخلي");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedInsideCategoryForCard3 = null;
            }
        });
    }

    private void setupTextWatchers() {
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

        // للبطاقة الأولى
        firstCategoryAdapter1 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryNames);
        firstCategoryAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerFirstCategory.setAdapter(firstCategoryAdapter1);

        // للبطاقة الثانية
        firstCategoryAdapter2 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryNames);
        firstCategoryAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerCategoryFirst.setAdapter(firstCategoryAdapter2);

        // للبطاقة الثالثة
        firstCategoryAdapter3 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryNames);
        firstCategoryAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerFirstForProduct.setAdapter(firstCategoryAdapter3);
    }

    private void updateSecondCategorySpinnersForCard2() {
        List<String> categoryNames = new ArrayList<>();
        for (table_secondCategory category : secondCategoryListForCard2) {
            categoryNames.add(category.name);
        }

        if (categoryNames.isEmpty()) {
            categoryNames.add("لا توجد تصنيفات فرعية");
        }

        secondCategoryAdapter2 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryNames);
        secondCategoryAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerCategorySecond.setAdapter(secondCategoryAdapter2);
    }

    private void updateSecondCategorySpinnersForCard3() {
        List<String> categoryNames = new ArrayList<>();
        for (table_secondCategory category : secondCategoryListForCard3) {
            categoryNames.add(category.name);
        }

        if (categoryNames.isEmpty()) {
            categoryNames.add("لا توجد تصنيفات فرعية");
        }

        secondCategoryAdapter3 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryNames);
        secondCategoryAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerSecondForCategory.setAdapter(secondCategoryAdapter3);
    }

    private void updateInsideCategorySpinnersForCard3() {
        List<String> categoryNames = new ArrayList<>();
        for (table_CategorisInside category : insideCategoryListForCard3) {
            categoryNames.add(category.name);
        }

        if (categoryNames.isEmpty()) {
            categoryNames.add("لا توجد تصنيفات داخلية");
        }

        insideCategoryAdapter3 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryNames);
        insideCategoryAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinerCategoryIn.setAdapter(insideCategoryAdapter3);
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

        if (selectedFirstCategoryForCard1 == null) {
            showError("الرجاء اختيار التصنيف الأساسي");
            return;
        }

        table_secondCategory category = new table_secondCategory(name, selectedFirstCategoryForCard1.id, "");
        secondCategoryViewModel.insert(category);

        // تحديث القوائم
        if (selectedFirstCategoryForCard1 != null) {
            loadSecondCategoriesForCard2(selectedFirstCategoryForCard1.id);
            loadSecondCategoriesForCard3(selectedFirstCategoryForCard1.id);
        }

        showSuccess("تم إضافة التصنيف الفرعي: " + name + " إلى قسم " + selectedFirstCategoryForCard1.name);
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

        if (selectedFirstCategoryForCard2 == null) {
            showError("الرجاء اختيار التصنيف الأساسي");
            return;
        }

        if (selectedSecondCategoryForCard2 == null) {
            showError("الرجاء اختيار التصنيف الفرعي");
            return;
        }

        final int secondCategoryId = selectedSecondCategoryForCard2.id;
        final String secondCategoryName = selectedSecondCategoryForCard2.name;

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
                            binding.addCategoryIn.setEnabled(true);
                            binding.addCategoryIn.setText("إضافة");

                            // تحديث التصنيفات الداخلية للبطاقة الثالثة
                            if (selectedSecondCategoryForCard3 != null &&
                                    selectedSecondCategoryForCard3.id == secondCategoryId) {
                                loadInsideCategoriesForCard3(secondCategoryId);
                            }

                            showSuccess("تم إضافة التصنيف الداخلي: " + name +
                                    " إلى قسم " + secondCategoryName);
                            binding.nameCategoryIn.setText("");
                            binding.card2.setVisibility(View.GONE);
                        });
                    }
                });
    }

    // ========== البطاقة الثالثة: إضافة منتج ==========
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

        if (selectedFirstCategoryForCard3 == null) {
            showError("الرجاء اختيار التصنيف الأساسي");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int rate = rateStr.isEmpty() ? 0 : Integer.parseInt(rateStr);

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
            product.setFirstCategoryId(selectedFirstCategoryForCard3.id);
            product.setSecondCategoryId(selectedSecondCategoryForCard3 != null ? selectedSecondCategoryForCard3.id : 0);
            product.setInsideCategoryId(selectedInsideCategoryForCard3 != null ? selectedInsideCategoryForCard3.id : 0);
            product.setActive(true);
            product.setFeatured(false);
            product.setQuantity(100);
            product.setImage(imagePathToSave);
            product.setDiscount(0);
            product.setSoldCount(0);

            productViewModel.insertProduct(product);

            String categoryPath = selectedFirstCategoryForCard3.name;
            if (selectedSecondCategoryForCard3 != null) {
                categoryPath += " → " + selectedSecondCategoryForCard3.name;
            }
            if (selectedInsideCategoryForCard3 != null) {
                categoryPath += " → " + selectedInsideCategoryForCard3.name;
            }

            showSuccess("تم إضافة المنتج: " + name + " إلى " + categoryPath);
            clearProductFields();
            binding.card3.setVisibility(View.GONE);

        } catch (NumberFormatException e) {
            showError("الرجاء إدخال أرقام صحيحة");
        }
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            String fileName = "product_" + System.currentTimeMillis() + ".jpg";
            File imagesDir = new File(getFilesDir(), "product_images");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }
            File imageFile = new File(imagesDir, fileName);
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            String savedPath = imageFile.getAbsolutePath();
            Log.d("Admen", "تم حفظ الصورة في: " + savedPath);
            return savedPath;
        } catch (Exception e) {
            e.printStackTrace();
            showError("خطأ في حفظ الصورة: " + e.getMessage());
            return "";
        }
    }

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