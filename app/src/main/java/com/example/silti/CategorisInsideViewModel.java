package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class CategorisInsideViewModel extends AndroidViewModel {
    private CategorisInsideRepository categorisInsideRepository;

    // للبطاقة الثالثة (إضافة منتج)
    private MutableLiveData<Integer> currentSecondCategoryIdForCard3 = new MutableLiveData<>();
    private LiveData<List<table_CategorisInside>> categoriesBySecondCategoryForCard3;

    // للبطاقة الثانية (بعد إضافة تصنيف داخلي - للتحديث)
    private MutableLiveData<Integer> currentSecondCategoryIdForUpdate = new MutableLiveData<>();

    public CategorisInsideViewModel(Application application) {
        super(application);
        categorisInsideRepository = new CategorisInsideRepository(application);
    }

    // ========== للبطاقة الثالثة (إضافة منتج) ==========
    public void setCurrentSecondCategoryIdForCard3(int secondCategoryId) {
        currentSecondCategoryIdForCard3.postValue(secondCategoryId);
        categoriesBySecondCategoryForCard3 = categorisInsideRepository.getCategoriesBySecondCategory(secondCategoryId);
    }

    public LiveData<List<table_CategorisInside>> getCategoriesBySecondCategoryForCard3() {
        return categoriesBySecondCategoryForCard3;
    }

    // ========== للبطاقة الثانية (بعد إضافة تصنيف داخلي) ==========
    public void setCurrentSecondCategoryIdForUpdate(int secondCategoryId) {
        currentSecondCategoryIdForUpdate.postValue(secondCategoryId);
    }

    public LiveData<List<table_CategorisInside>> getCategoriesBySecondCategoryForUpdate() {
        Integer id = currentSecondCategoryIdForUpdate.getValue();
        if (id != null) {
            return categorisInsideRepository.getCategoriesBySecondCategory(id);
        }
        return new MutableLiveData<>(null);
    }

    // ========== دوال الإدراج والتحديث المشتركة ==========
    public void insert(table_CategorisInside category) {
        categorisInsideRepository.insert(category);
    }

    public void insert(int secondCategoryId, String name, String icon, int position) {
        table_CategorisInside category = new table_CategorisInside(secondCategoryId, name, icon, position);
        categorisInsideRepository.insert(category);
    }

    public void insertAll(List<table_CategorisInside> categories) {
        categorisInsideRepository.insertAll(categories);
    }

    public void insertWithAutoPosition(int secondCategoryId, String name, String icon) {
        categorisInsideRepository.insertWithAutoPosition(secondCategoryId, name, icon);
    }

    public void update(table_CategorisInside category) {
        categorisInsideRepository.update(category);
    }

    public void updateCategoryStatus(int categoryId, boolean isActive) {
        categorisInsideRepository.updateCategoryStatus(categoryId, isActive);
    }

    public void updatePosition(int categoryId, int position) {
        categorisInsideRepository.updatePosition(categoryId, position);
    }

    public void delete(table_CategorisInside category) {
        categorisInsideRepository.delete(category);
    }

    public void deleteById(int categoryId) {
        categorisInsideRepository.deleteById(categoryId);
    }

    public void deleteBySecondCategory(int secondCategoryId) {
        categorisInsideRepository.deleteBySecondCategory(secondCategoryId);
    }

    public LiveData<table_CategorisInside> getCategoryById(int categoryId) {
        return categorisInsideRepository.getCategoryById(categoryId);
    }

    public LiveData<List<table_CategorisInside>> getAllCategoriesBySecondCategory(int secondCategoryId) {
        return categorisInsideRepository.getAllCategoriesBySecondCategory(secondCategoryId);
    }

    public void getActiveCountBySecondCategory(int secondCategoryId, CategorisInsideRepository.CountCallback callback) {
        categorisInsideRepository.getActiveCountBySecondCategory(secondCategoryId, callback);
    }

    public void getMaxPositionBySecondCategory(int secondCategoryId, CategorisInsideRepository.PositionCallback callback) {
        categorisInsideRepository.getMaxPositionBySecondCategory(secondCategoryId, callback);
    }
}