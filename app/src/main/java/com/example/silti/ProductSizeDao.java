package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProductSizeDao {
    @Insert
    void insert(table_productSizes productSize);

    @Insert
    void insertAll(List<table_productSizes> productSizes);

    @Query("SELECT s.* FROM sizes s INNER JOIN product_sizes ps ON s.id = ps.sizeId WHERE ps.productId = :productId")
    LiveData<List<table_sizes>> getSizesForProduct(long productId);

    @Query("SELECT quantity FROM product_sizes WHERE productId = :productId AND sizeId = :sizeId")
    int getQuantityForSize(long productId, int sizeId);

    @Query("DELETE FROM product_sizes WHERE productId = :productId")
    void deleteByProductId(long productId);
}
