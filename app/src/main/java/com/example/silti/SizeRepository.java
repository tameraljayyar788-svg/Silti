package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SizeRepository {
    private SizeDao sizeDao;
    private ExecutorService executorService;

    public SizeRepository(Application application) {
        AppDataBase db = AppDataBase.getInstance(application);
        sizeDao = db.sizeDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Insert
    public void insert(table_sizes size) {
        executorService.execute(() -> sizeDao.insert(size));
    }

    public void insert(String sizeName, String category) {
        table_sizes size = new table_sizes(sizeName, category);
        executorService.execute(() -> sizeDao.insert(size));
    }

    // Read
    public LiveData<List<table_sizes>> getAllSizes() {
        return sizeDao.getAllSizes();
    }

    public LiveData<List<table_sizes>> getSizesByCategory(String category) {
        return sizeDao.getSizesByCategory(category);
    }

    public LiveData<table_sizes> getSizeByName(String sizeName) {
        return sizeDao.getSizeByName(sizeName);
    }
}
