package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class FirstCategoryViewModel extends AndroidViewModel {
    private FirstCategoryRepository firstCategoryRepository;
    private LiveData<List<table_firstCategory>> allActiveCategories;

    public FirstCategoryViewModel(Application application) {
        super(application);
        firstCategoryRepository = new FirstCategoryRepository(application);
        allActiveCategories = firstCategoryRepository.getAllActiveCategories();
    }

    // Read
    public LiveData<List<table_firstCategory>> getAllActiveCategories() {
        return allActiveCategories;
    }

    public LiveData<List<table_firstCategory>> getAllCategories() {
        return firstCategoryRepository.getAllCategories();
    }

    public LiveData<table_firstCategory> getCategoryById(int categoryId) {
        return firstCategoryRepository.getCategoryById(categoryId);
    }

    public LiveData<List<table_firstCategory>> searchCategories(String query) {
        return firstCategoryRepository.searchCategories(query);
    }

    // Insert
    public void insert(String name, String icon) {
        firstCategoryRepository.getMaxPosition(new FirstCategoryRepository.MaxPositionCallback() {
            @Override
            public void onResult(int maxPosition) {
                firstCategoryRepository.insert(name, icon, maxPosition + 1);
            }
        });
    }

    public void insert(table_firstCategory category) {
        firstCategoryRepository.insert(category);
    }

    // Update
    public void update(table_firstCategory category) {
        firstCategoryRepository.update(category);
    }

    public void updatePosition(int categoryId, int position) {
        firstCategoryRepository.updatePosition(categoryId, position);
    }

    public void updateCategoryStatus(int categoryId, boolean isActive) {
        firstCategoryRepository.updateCategoryStatus(categoryId, isActive);
    }

    // Delete
    public void delete(table_firstCategory category) {
        firstCategoryRepository.delete(category);
    }

    public void deleteById(int categoryId) {
        firstCategoryRepository.deleteById(categoryId);
    }
}
