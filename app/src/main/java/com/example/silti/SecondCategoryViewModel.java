package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class SecondCategoryViewModel extends AndroidViewModel {
    private SecondCategoryRepository secondCategoryRepository;

    // للبطاقة الثالثة (إضافة منتج)
    private MutableLiveData<Integer> currentFirstCategoryIdForCard3 = new MutableLiveData<>();
    private LiveData<List<table_secondCategory>> categoriesByFirstCategoryForCard3;

    // للبطاقة الثانية (إضافة تصنيف داخلي)
    private MutableLiveData<Integer> currentFirstCategoryIdForCard2 = new MutableLiveData<>();
    private LiveData<List<table_secondCategory>> categoriesByFirstCategoryForCard2;

    public SecondCategoryViewModel(Application application) {
        super(application);
        secondCategoryRepository = new SecondCategoryRepository(application);
    }

    // ========== للبطاقة الثالثة (إضافة منتج) ==========
    public void setCurrentFirstCategoryIdForCard3(int firstCategoryId) {
        currentFirstCategoryIdForCard3.postValue(firstCategoryId);
        categoriesByFirstCategoryForCard3 = secondCategoryRepository.getCategoriesByFirstCategory(firstCategoryId);
    }

    public LiveData<List<table_secondCategory>> getCategoriesByFirstCategoryForCard3() {
        return categoriesByFirstCategoryForCard3;
    }

    // ========== للبطاقة الثانية (إضافة تصنيف داخلي) ==========
    public void setCurrentFirstCategoryIdForCard2(int firstCategoryId) {
        currentFirstCategoryIdForCard2.postValue(firstCategoryId);
        categoriesByFirstCategoryForCard2 = secondCategoryRepository.getCategoriesByFirstCategory(firstCategoryId);
    }

    public LiveData<List<table_secondCategory>> getCategoriesByFirstCategoryForCard2() {
        return categoriesByFirstCategoryForCard2;
    }

    // ========== دوال الإدراج والتحديث المشتركة ==========
    public void insert(table_secondCategory category) {
        secondCategoryRepository.insert(category);
    }

    public void insert(String name, int firstCategoryId, String icon) {
        table_secondCategory category = new table_secondCategory(name, firstCategoryId, icon);
        secondCategoryRepository.insert(category);
    }

    public void update(table_secondCategory category) {
        secondCategoryRepository.update(category);
    }

    public void updateCategoryStatus(int categoryId, boolean isActive) {
        secondCategoryRepository.updateCategoryStatus(categoryId, isActive);
    }

    public void delete(table_secondCategory category) {
        secondCategoryRepository.delete(category);
    }

    public void deleteById(int categoryId) {
        secondCategoryRepository.deleteById(categoryId);
    }

    public void deleteByFirstCategory(int firstCategoryId) {
        secondCategoryRepository.deleteByFirstCategory(firstCategoryId);
    }

    public LiveData<table_secondCategory> getCategoryById(int categoryId) {
        return secondCategoryRepository.getCategoryById(categoryId);
    }

    public LiveData<List<table_secondCategory>> getAllCategoriesByFirstCategory(int firstCategoryId) {
        return secondCategoryRepository.getAllCategoriesByFirstCategory(firstCategoryId);
    }

    public void getActiveCountByFirstCategory(int firstCategoryId, SecondCategoryRepository.CountCallback callback) {
        secondCategoryRepository.getActiveCountByFirstCategory(firstCategoryId, callback);
    }
}