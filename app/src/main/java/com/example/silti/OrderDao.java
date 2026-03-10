package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    long insertOrder(table_order order);

    @Update
    void updateOrder(table_order order);

    @Query("SELECT * FROM orders WHERE id = :orderId")
    LiveData<table_order> getOrderById(long orderId);

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    LiveData<List<table_order>> getAllOrders();

    @Query("SELECT * FROM orders WHERE orderStatus = :status ORDER BY createdAt DESC")
    LiveData<List<table_order>> getOrdersByStatus(String status);

    @Query("SELECT COUNT(*) FROM orders WHERE orderStatus = :status")
    int getOrderCountByStatus(String status);
}
