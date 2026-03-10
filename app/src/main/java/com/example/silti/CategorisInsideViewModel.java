package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class CategorisInsideViewModel extends AndroidViewModel {
    private CategorisInsideRepository categorisInsideRepository;
    private MutableLiveData<Integer> currentSecondCategoryId = new MutableLiveData<>();
    private LiveData<List<table_CategorisInside>> categoriesBySecondCategory;

    public CategorisInsideViewModel(Application application) {
        super(application);
        categorisInsideRepository = new CategorisInsideRepository(application);
    }

    public void setCurrentSecondCategoryId(int secondCategoryId) {
        currentSecondCategoryId.setValue(secondCategoryId);
        categoriesBySecondCategory = categorisInsideRepository.getCategoriesBySecondCategory(secondCategoryId);
    }

    // Read
    public LiveData<List<table_CategorisInside>> getCategoriesBySecondCategory() {
        return categoriesBySecondCategory;
    }

    public LiveData<List<table_CategorisInside>> getAllCategoriesBySecondCategory(int secondCategoryId) {
        return categorisInsideRepository.getAllCategoriesBySecondCategory(secondCategoryId);
    }

    public LiveData<table_CategorisInside> getCategoryById(int categoryId) {
        return categorisInsideRepository.getCategoryById(categoryId);
    }

    // Insert
    public void insert(String name, String icon) {
        Integer secondCategoryId = currentSecondCategoryId.getValue();
        if (secondCategoryId != null) {
            categorisInsideRepository.insertWithAutoPosition(secondCategoryId, name, icon);
        }
    }

    public void insert(table_CategorisInside category) {
        categorisInsideRepository.insert(category);
    }

    public void insert(int secondCategoryId, String name, String icon, int position) {
        categorisInsideRepository.insert(secondCategoryId, name, icon, position);
    }

    public void insertAll(List<table_CategorisInside> categories) {
        categorisInsideRepository.insertAll(categories);
    }

    // Update
    public void update(table_CategorisInside category) {
        categorisInsideRepository.update(category);
    }

    public void updateCategoryStatus(int categoryId, boolean isActive) {
        categorisInsideRepository.updateCategoryStatus(categoryId, isActive);
    }

    public void updatePosition(int categoryId, int position) {
        categorisInsideRepository.updatePosition(categoryId, position);
    }

    // Delete
    public void delete(table_CategorisInside category) {
        categorisInsideRepository.delete(category);
    }

    public void deleteById(int categoryId) {
        categorisInsideRepository.deleteById(categoryId);
    }

    public void deleteBySecondCategory(int secondCategoryId) {
        categorisInsideRepository.deleteBySecondCategory(secondCategoryId);
    }

    // Callbacks
    public void getActiveCountBySecondCategory(int secondCategoryId, CategorisInsideRepository.CountCallback callback) {
        categorisInsideRepository.getActiveCountBySecondCategory(secondCategoryId, callback);
    }

    public void getMaxPositionBySecondCategory(int secondCategoryId, CategorisInsideRepository.PositionCallback callback) {
        categorisInsideRepository.getMaxPositionBySecondCategory(secondCategoryId, callback);
    }
}
