package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryRepository {
    private CategoryDao categoryDao;
    private ExecutorService executorService;

    public CategoryRepository(Application application) {
        AppDataBase db = AppDataBase.getInstance(application);
        categoryDao = db.categoryDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Insert
    public void insertCategory(table_category category) {
        executorService.execute(() -> categoryDao.insertCategory(category));
    }

    // Update
    public void updateCategory(table_category category) {
        executorService.execute(() -> categoryDao.updateCategory(category));
    }

    // Delete
    public void deleteCategory(table_category category) {
        executorService.execute(() -> categoryDao.deleteCategory(category));
    }

    // Read
    public LiveData<List<table_category>> getMainCategories() {
        return categoryDao.getMainCategories();
    }

    public LiveData<List<table_category>> getSubCategories(int parentId) {
        return categoryDao.getSubCategories(parentId);
    }

    public LiveData<table_category> getCategoryById(int categoryId) {
        return categoryDao.getCategoryById(categoryId);
    }

    public LiveData<List<table_category>> getAllActiveCategories() {
        return categoryDao.getAllActiveCategories();
    }
}
