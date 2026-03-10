package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ProductSizeViewModel extends AndroidViewModel {
    private ProductSizeRepository productSizeRepository;
    private MutableLiveData<Long> currentProductId = new MutableLiveData<>();
    private LiveData<List<table_sizes>> sizesForProduct;

    public ProductSizeViewModel(Application application) {
        super(application);
        productSizeRepository = new ProductSizeRepository(application);
    }

    public void setCurrentProductId(long productId) {
        currentProductId.setValue(productId);
        sizesForProduct = productSizeRepository.getSizesForProduct(productId);
    }

    // Get sizes for current product
    public LiveData<List<table_sizes>> getSizesForCurrentProduct() {
        return sizesForProduct;
    }

    public LiveData<List<table_sizes>> getSizesForProduct(long productId) {
        return productSizeRepository.getSizesForProduct(productId);
    }

    // Get quantity for specific size
    public void getQuantityForSize(int sizeId, ProductSizeRepository.QuantityCallback callback) {
        Long productId = currentProductId.getValue();
        if (productId != null) {
            productSizeRepository.getQuantityForSize(productId, sizeId, callback);
        }
    }

    // Insert
    public void insert(table_productSizes productSize) {
        productSizeRepository.insert(productSize);
    }

    public void insertAll(List<table_productSizes> productSizes) {
        productSizeRepository.insertAll(productSizes);
    }

    public void insertProductSizes(List<Integer> sizeIds, List<Integer> quantities) {
        Long productId = currentProductId.getValue();
        if (productId != null) {
            productSizeRepository.insertProductSizes(productId, sizeIds, quantities);
        }
    }

    // Delete
    public void deleteByProductId(long productId) {
        productSizeRepository.deleteByProductId(productId);
    }

    public void deleteForCurrentProduct() {
        Long productId = currentProductId.getValue();
        if (productId != null) {
            productSizeRepository.deleteByProductId(productId);
        }
    }
}
