package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SecondCategoryRepository {
    private SecondCategoryDao dao;
    private ExecutorService executorService;

    public SecondCategoryRepository(Application application) {
        AppDataBase db = AppDataBase.getInstance(application);
        dao = db.secondCategoryDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(table_secondCategory category) {
        executorService.execute(() -> dao.insert(category));
    }

    public void insert(String name, int firstCategoryId, String icon) {
        table_secondCategory category = new table_secondCategory(name, firstCategoryId, icon);
        executorService.execute(() -> dao.insert(category));
    }

    public void update(table_secondCategory category) {
        executorService.execute(() -> dao.update(category));
    }

    public void updateCategoryStatus(int categoryId, boolean isActive) {
        executorService.execute(() -> dao.updateCategoryStatus(categoryId, isActive));
    }

    public void delete(table_secondCategory category) {
        executorService.execute(() -> dao.delete(category));
    }

    public void deleteById(int categoryId) {
        executorService.execute(() -> dao.deleteById(categoryId));
    }

    public void deleteByFirstCategory(int firstCategoryId) {
        executorService.execute(() -> dao.deleteByFirstCategory(firstCategoryId));
    }

    public LiveData<List<table_secondCategory>> getCategoriesByFirstCategory(int firstCategoryId) {
        return dao.getCategoriesByFirstCategory(firstCategoryId);
    }

    public LiveData<table_secondCategory> getCategoryById(int categoryId) {
        return dao.getCategoryById(categoryId);
    }

    public LiveData<List<table_secondCategory>> getAllCategoriesByFirstCategory(int firstCategoryId) {
        return dao.getAllCategoriesByFirstCategory(firstCategoryId);
    }

    public void getActiveCountByFirstCategory(int firstCategoryId, CountCallback callback) {
        executorService.execute(() -> {
            int count = dao.getActiveCountByFirstCategory(firstCategoryId);
            callback.onResult(count);
        });
    }

    public interface CountCallback {
        void onResult(int count);
    }
}