package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CartDao {
    @Insert
    long insertCartItem(table_cart cartItem);

    @Update
    void updateCartItem(table_cart cartItem);

    @Delete
    void deleteCartItem(table_cart cartItem);

    @Query("DELETE FROM cart WHERE userId = :userId")
    void clearCart(long userId);

    @Query("SELECT * FROM cart WHERE userId = :userId ORDER BY addedAt DESC")
    LiveData<List<table_cart>> getCartItemsByUser(long userId);

    @Query("SELECT * FROM cart WHERE userId = :userId AND productId = :productId LIMIT 1")
    table_cart getCartItemByProductId(long userId, long productId);

    @Query("SELECT COUNT(*) FROM cart WHERE userId = :userId")
    LiveData<Integer> getCartItemCount(long userId);

    @Query("SELECT SUM(price * quantity) FROM cart WHERE userId = :userId")
    LiveData<Double> getCartTotalPrice(long userId);

    @Query("UPDATE cart SET quantity = :quantity WHERE id = :cartItemId")
    void updateQuantity(int cartItemId, int quantity);

    @Query("DELETE FROM cart WHERE userId = :userId AND productId = :productId")
    void removeFromCart(long userId, long productId);

    @Query("SELECT EXISTS(SELECT 1 FROM cart WHERE userId = :userId AND productId = :productId)")
    boolean isInCart(long userId, long productId);

}
