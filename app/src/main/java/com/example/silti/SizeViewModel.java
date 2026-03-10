package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class SizeViewModel extends AndroidViewModel {
    private SizeRepository sizeRepository;
    private MutableLiveData<String> currentCategory = new MutableLiveData<>();

    public SizeViewModel(Application application) {
        super(application);
        sizeRepository = new SizeRepository(application);
    }

    // All sizes
    public LiveData<List<table_sizes>> getAllSizes() {
        return sizeRepository.getAllSizes();
    }

    // Filter by category
    public void setCurrentCategory(String category) {
        currentCategory.setValue(category);
    }

    public LiveData<List<table_sizes>> getSizesByCategory() {
        return sizeRepository.getSizesByCategory(currentCategory.getValue());
    }

    public LiveData<List<table_sizes>> getSizesByCategory(String category) {
        return sizeRepository.getSizesByCategory(category);
    }

    // Get single size
    public LiveData<table_sizes> getSizeByName(String sizeName) {
        return sizeRepository.getSizeByName(sizeName);
    }

    // Insert
    public void insert(String sizeName, String category) {
        sizeRepository.insert(sizeName, category);
    }

    public void insert(table_sizes size) {
        sizeRepository.insert(size);
    }
}
