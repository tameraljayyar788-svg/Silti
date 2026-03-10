package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductSizeRepository {
    private ProductSizeDao productSizeDao;
    private ExecutorService executorService;

    public ProductSizeRepository(Application application) {
        AppDataBase db = AppDataBase.getInstance(application);
        productSizeDao = db.productSizeDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Insert
    public void insert(table_productSizes productSize) {
        executorService.execute(() -> productSizeDao.insert(productSize));
    }

    public void insertAll(List<table_productSizes> productSizes) {
        executorService.execute(() -> productSizeDao.insertAll(productSizes));
    }

    public void insertProductSizes(long productId, List<Integer> sizeIds, List<Integer> quantities) {
        executorService.execute(() -> {
            for (int i = 0; i < sizeIds.size(); i++) {
                table_productSizes productSize = new table_productSizes(
                        productId, sizeIds.get(i), quantities.get(i)
                );
                productSizeDao.insert(productSize);
            }
        });
    }

    // Read
    public LiveData<List<table_sizes>> getSizesForProduct(long productId) {
        return productSizeDao.getSizesForProduct(productId);
    }

    public void getQuantityForSize(long productId, int sizeId, QuantityCallback callback) {
        executorService.execute(() -> {
            int quantity = productSizeDao.getQuantityForSize(productId, sizeId);
            callback.onResult(quantity);
        });
    }

    // Delete
    public void deleteByProductId(long productId) {
        executorService.execute(() -> productSizeDao.deleteByProductId(productId));
    }

    public interface QuantityCallback { void onResult(int quantity); }
}
