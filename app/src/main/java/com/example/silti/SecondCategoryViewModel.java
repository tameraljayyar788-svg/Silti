package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class SecondCategoryViewModel extends AndroidViewModel {
    private SecondCategoryRepository secondCategoryRepository;
    private MutableLiveData<Integer> currentFirstCategoryId = new MutableLiveData<>();

    public SecondCategoryViewModel(Application application) {
        super(application);
        secondCategoryRepository = new SecondCategoryRepository(application);
    }

    public void setCurrentFirstCategoryId(int firstCategoryId) {
        // ✅ استخدام postValue بدلاً من setValue
        currentFirstCategoryId.postValue(firstCategoryId);
    }

    public LiveData<List<table_secondCategory>> getCategoriesByFirstCategory() {
        Integer id = currentFirstCategoryId.getValue();
        if (id != null) {
            return secondCategoryRepository.getCategoriesByFirstCategory(id);
        }
        return new MutableLiveData<>(null);
    }

    public LiveData<List<table_secondCategory>> getAllCategoriesByFirstCategory(int firstCategoryId) {
        return secondCategoryRepository.getAllCategoriesByFirstCategory(firstCategoryId);
    }

    public LiveData<table_secondCategory> getCategoryById(int categoryId) {
        return secondCategoryRepository.getCategoryById(categoryId);
    }

    public void insert(String name, String icon) {
        Integer firstCategoryId = currentFirstCategoryId.getValue();
        if (firstCategoryId != null) {
            secondCategoryRepository.insert(name, firstCategoryId, icon);
        }
    }

    public void insert(table_secondCategory category) {
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

    public void getActiveCountByFirstCategory(int firstCategoryId, SecondCategoryRepository.CountCallback callback) {
        secondCategoryRepository.getActiveCountByFirstCategory(firstCategoryId, callback);
    }
}