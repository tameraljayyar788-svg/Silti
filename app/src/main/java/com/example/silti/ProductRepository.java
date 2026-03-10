package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductRepository {
        private ProductDao productDao;
        private ExecutorService executorService;

        public ProductRepository(Application application) {
            AppDataBase db = AppDataBase.getInstance(application);
            productDao = db.productDao();
            executorService = Executors.newSingleThreadExecutor();
        }

        // Insert
        public void insert(table_product product) {
            executorService.execute(() -> productDao.insertProduct(product));
        }

        // Update
        public void update(table_product product) {
            executorService.execute(() -> productDao.updateProduct(product));
        }

        // Delete
        public void delete(table_product product) {
            executorService.execute(() -> productDao.deleteProduct(product));
        }

        // Read
        public LiveData<table_product> getProductById(long productId) {
            return productDao.getProductById(productId);
        }

        public LiveData<List<table_product>> getProductsByFirstCategory(int categoryId) {
            return productDao.getProductsByFirstCategory(categoryId);
        }

        public LiveData<List<table_product>> getProductsBySecondCategory(int categoryId) {
            return productDao.getProductsBySecondCategory(categoryId);
        }

        public LiveData<List<table_product>> getProductsByInsideCategory(int categoryId) {
            return productDao.getProductsByInsideCategory(categoryId);
        }

        public LiveData<List<table_product>> getDiscountedProducts() {
            return productDao.getProductsWithDiscount();
        }

        public LiveData<List<table_product>> getFeaturedProducts() {
            return productDao.getFeaturedProducts();
        }

        public LiveData<List<table_product>> getBestSellingProducts() {
            return productDao.getBestSellingProducts();
        }

        public LiveData<List<table_product>> getTopRatedProducts() {
            return productDao.getTopRatedProducts();
        }

        public LiveData<List<table_product>> searchProducts(String query) {
            return productDao.searchProducts(query);
        }

        public LiveData<List<table_product>> getProductsByBrand(String brand) {
            return productDao.getProductsByBrand(brand);
        }

        public LiveData<List<table_product>> getAllActiveProducts() {
            return productDao.getAllActiveProducts();
        }

        // Actions
        public void incrementSoldCount(long productId) {
            executorService.execute(() -> productDao.incrementSoldCount(productId));
        }

        public void decreaseQuantity(long productId, int quantity) {
            executorService.execute(() -> productDao.decreaseQuantity(productId, quantity));
        }

        // Callback
        public void getProductCountByFirstCategory(int categoryId, CountCallback callback) {
            executorService.execute(() -> {
                int count = productDao.getProductCountByFirstCategory(categoryId);
                callback.onResult(count);
            });
        }

        public interface CountCallback { void onResult(int count); }
    }
