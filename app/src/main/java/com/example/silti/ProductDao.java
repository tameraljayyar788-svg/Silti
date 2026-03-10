package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    long insertProduct(table_product product);

    @Update
    void updateProduct(table_product product);

    @Delete
    void deleteProduct(table_product product);

    @Query("SELECT * FROM products WHERE id = :productId")
    LiveData<table_product> getProductById(long productId);

    @Query("SELECT * FROM products WHERE firstCategoryId = :categoryId AND isActive = 1")
    LiveData<List<table_product>> getProductsByFirstCategory(int categoryId);

    @Query("SELECT * FROM products WHERE secondCategoryId = :categoryId AND isActive = 1")
    LiveData<List<table_product>> getProductsBySecondCategory(int categoryId);

    @Query("SELECT * FROM products WHERE insideCategoryId = :categoryId AND isActive = 1")
    LiveData<List<table_product>> getProductsByInsideCategory(int categoryId);

    @Query("SELECT * FROM products WHERE discount > 0 AND isActive = 1")
    LiveData<List<table_product>> getProductsWithDiscount();

    @Query("SELECT * FROM products WHERE isFeatured = 1 AND isActive = 1")
    LiveData<List<table_product>> getFeaturedProducts();

    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY soldCount DESC LIMIT 10")
    LiveData<List<table_product>> getBestSellingProducts();

    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY rate DESC LIMIT 10")
    LiveData<List<table_product>> getTopRatedProducts();

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' AND isActive = 1")
    LiveData<List<table_product>> searchProducts(String query);

    @Query("SELECT * FROM products WHERE brand = :brand AND isActive = 1")
    LiveData<List<table_product>> getProductsByBrand(String brand);

    @Query("SELECT * FROM products WHERE isActive = 1")
    LiveData<List<table_product>> getAllActiveProducts();

    @Query("SELECT COUNT(*) FROM products WHERE firstCategoryId = :categoryId")
    int getProductCountByFirstCategory(int categoryId);

    @Query("UPDATE products SET soldCount = soldCount + 1 WHERE id = :productId")
    void incrementSoldCount(long productId);

    @Query("UPDATE products SET quantity = quantity - :quantity WHERE id = :productId")
    void decreaseQuantity(long productId, int quantity);
}
