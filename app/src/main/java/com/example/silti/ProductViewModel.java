package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {
    private ProductRepository productRepository;
    private LiveData<List<table_product>> allProducts;
    private MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private MutableLiveData<Integer> currentFirstCategoryId = new MutableLiveData<>();
    private MutableLiveData<Integer> currentSecondCategoryId = new MutableLiveData<>();
    private MutableLiveData<Integer> currentInsideCategoryId = new MutableLiveData<>();

    public ProductViewModel(Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        allProducts = productRepository.getAllActiveProducts();
    }

    // All products
    public LiveData<List<table_product>> getAllProducts() {
        return allProducts;
    }

    // Product by ID
    public LiveData<table_product> getProductById(long productId) {
        return productRepository.getProductById(productId);
    }

    // Filter by categories
    public void setCurrentFirstCategoryId(int categoryId) {
        currentFirstCategoryId.setValue(categoryId);
    }

    public LiveData<List<table_product>> getProductsByFirstCategory() {
        return Transformations.switchMap(currentFirstCategoryId,
                id -> productRepository.getProductsByFirstCategory(id));
    }

    public void setCurrentSecondCategoryId(int categoryId) {
        currentSecondCategoryId.setValue(categoryId);
    }

    public LiveData<List<table_product>> getProductsBySecondCategory() {
        return Transformations.switchMap(currentSecondCategoryId,
                id -> productRepository.getProductsBySecondCategory(id));
    }

    public void setCurrentInsideCategoryId(int categoryId) {
        currentInsideCategoryId.setValue(categoryId);
    }

    public LiveData<List<table_product>> searchProducts(String query) {
        return productRepository.searchProducts(query);
    }

    public LiveData<List<table_product>> getProductsByInsideCategory() {
        return Transformations.switchMap(currentInsideCategoryId,
                id -> productRepository.getProductsByInsideCategory(id));
    }

    // Special product lists
    public LiveData<List<table_product>> getDiscountedProducts() {
        return productRepository.getDiscountedProducts();
    }

    public LiveData<List<table_product>> getFeaturedProducts() {
        return productRepository.getFeaturedProducts();
    }

    public LiveData<List<table_product>> getBestSellingProducts() {
        return productRepository.getBestSellingProducts();
    }

    public LiveData<List<table_product>> getTopRatedProducts() {
        return productRepository.getTopRatedProducts();
    }

    public LiveData<List<table_product>> getProductsByBrand(String brand) {
        return productRepository.getProductsByBrand(brand);
    }

    // Search
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<List<table_product>> getSearchResults() {
        return Transformations.switchMap(searchQuery,
                query -> productRepository.searchProducts(query));
    }

    // Admin actions
    public void insertProduct(table_product product) {
        productRepository.insert(product);
    }

    public void updateProduct(table_product product) {
        productRepository.update(product);
    }

    public void deleteProduct(table_product product) {
        productRepository.delete(product);
    }

    // Product actions
    public void incrementSoldCount(long productId) {
        productRepository.incrementSoldCount(productId);
    }

    public void decreaseQuantity(long productId, int quantity) {
        productRepository.decreaseQuantity(productId, quantity);
    }

    // Callback
    public void getProductCountByFirstCategory(int categoryId, ProductRepository.CountCallback callback) {
        productRepository.getProductCountByFirstCategory(categoryId, callback);
    }
}
