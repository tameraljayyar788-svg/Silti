package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategorisInsideRepository {
    private CategorisInsideDao dao;
    private ExecutorService executorService;

    public CategorisInsideRepository(Application application) {
        AppDataBase db = AppDataBase.getInstance(application);
        dao = db.categorisInsideDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(table_CategorisInside category) {
        executorService.execute(() -> dao.insert(category));
    }

    public void insert(int secondCategoryId, String name, String icon, int position) {
        table_CategorisInside category = new table_CategorisInside(secondCategoryId, name, icon, position);
        executorService.execute(() -> dao.insert(category));
    }

    public void insertAll(List<table_CategorisInside> categories) {
        executorService.execute(() -> {
            for (table_CategorisInside category : categories) {
                dao.insert(category);
            }
        });
    }

    public void update(table_CategorisInside category) {
        executorService.execute(() -> dao.update(category));
    }

    public void updateCategoryStatus(int categoryId, boolean isActive) {
        executorService.execute(() -> dao.updateCategoryStatus(categoryId, isActive));
    }

    public void updatePosition(int categoryId, int position) {
        executorService.execute(() -> dao.updatePosition(categoryId, position));
    }

    public void delete(table_CategorisInside category) {
        executorService.execute(() -> dao.delete(category));
    }

    public void deleteById(int categoryId) {
        executorService.execute(() -> dao.deleteById(categoryId));
    }

    public void deleteBySecondCategory(int secondCategoryId) {
        executorService.execute(() -> dao.deleteBySecondCategory(secondCategoryId));
    }

    public LiveData<List<table_CategorisInside>> getCategoriesBySecondCategory(int secondCategoryId) {
        return dao.getCategoriesBySecondCategory(secondCategoryId);
    }

    public LiveData<table_CategorisInside> getCategoryById(int categoryId) {
        return dao.getCategoryById(categoryId);
    }

    public LiveData<List<table_CategorisInside>> getAllCategoriesBySecondCategory(int secondCategoryId) {
        return dao.getAllCategoriesBySecondCategory(secondCategoryId);
    }

    public void getActiveCountBySecondCategory(int secondCategoryId, CountCallback callback) {
        executorService.execute(() -> {
            int count = dao.getActiveCountBySecondCategory(secondCategoryId);
            callback.onResult(count);
        });
    }

    public void getMaxPositionBySecondCategory(int secondCategoryId, PositionCallback callback) {
        executorService.execute(() -> {
            int max = dao.getMaxPositionBySecondCategory(secondCategoryId);
            callback.onResult(max);
        });
    }

    public void insertWithAutoPosition(int secondCategoryId, String name, String icon) {
        executorService.execute(() -> {
            int maxPosition = dao.getMaxPositionBySecondCategory(secondCategoryId);
            table_CategorisInside newCategory = new table_CategorisInside(
                    secondCategoryId, name, icon, maxPosition + 1
            );
            dao.insert(newCategory);
        });
    }

    public interface CountCallback { void onResult(int count); }
    public interface PositionCallback { void onResult(int position); }
}
