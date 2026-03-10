package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private CategoryRepository categoryRepository;

    public CategoryViewModel(Application application) {
        super(application);
        categoryRepository = new CategoryRepository(application);
    }

    // Read
    public LiveData<List<table_category>> getMainCategories() {
        return categoryRepository.getMainCategories();
    }

    public LiveData<List<table_category>> getSubCategories(int parentId) {
        return categoryRepository.getSubCategories(parentId);
    }

    public LiveData<table_category> getCategoryById(int categoryId) {
        return categoryRepository.getCategoryById(categoryId);
    }

    public LiveData<List<table_category>> getAllActiveCategories() {
        return categoryRepository.getAllActiveCategories();
    }

    // Insert
    public void insertCategory(table_category category) {
        categoryRepository.insertCategory(category);
    }

    // Update
    public void updateCategory(table_category category) {
        categoryRepository.updateCategory(category);
    }

    // Delete
    public void deleteCategory(table_category category) {
        categoryRepository.deleteCategory(category);
    }
}
