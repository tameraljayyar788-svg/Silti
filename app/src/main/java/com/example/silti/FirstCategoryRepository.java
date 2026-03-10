package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirstCategoryRepository {
    private FirstCategoryDao dao;
    private LiveData<List<table_firstCategory>> allCategories;
    private ExecutorService executorService;

    public FirstCategoryRepository(Application application) {
        AppDataBase db = AppDataBase.getInstance(application);
        dao = db.firstCategoryDao();
        allCategories = dao.getAllActiveCategories();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(table_firstCategory category) {
        executorService.execute(() -> dao.insert(category));
    }

    public void insert(String name, String icon, int position) {
        table_firstCategory category = new table_firstCategory(0, name, icon, position, true, System.currentTimeMillis());
        executorService.execute(() -> dao.insert(category));
    }

    public void update(table_firstCategory category) {
        executorService.execute(() -> dao.update(category));
    }

    public void updatePosition(int categoryId, int position) {
        executorService.execute(() -> dao.updatePosition(categoryId, position));
    }

    public void updateCategoryStatus(int categoryId, boolean isActive) {
        executorService.execute(() -> dao.updateCategoryStatus(categoryId, isActive));
    }

    public void delete(table_firstCategory category) {
        executorService.execute(() -> dao.delete(category));
    }

    public void deleteById(int categoryId) {
        executorService.execute(() -> dao.deleteById(categoryId));
    }

    public LiveData<List<table_firstCategory>> getAllActiveCategories() {
        return dao.getAllActiveCategories();
    }

    public LiveData<List<table_firstCategory>> getAllCategories() {
        return dao.getAllCategories();
    }

    public LiveData<table_firstCategory> getCategoryById(int categoryId) {
        return dao.getCategoryById(categoryId);
    }

    public LiveData<List<table_firstCategory>> searchCategories(String query) {
        return dao.searchCategories(query);
    }

    public void getMaxPosition(MaxPositionCallback callback) {
        executorService.execute(() -> {
            int max = dao.getMaxPosition();
            callback.onResult(max);
        });
    }

    public interface MaxPositionCallback {
        void onResult(int maxPosition);
    }
}
