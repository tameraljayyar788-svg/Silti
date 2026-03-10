package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OrderItemDao {
    @Insert
    void insertOrderItem(order_item item);

    @Insert
    void insertAllOrderItems(List<order_item> items);

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    LiveData<List<order_item>> getItemsForOrder(long orderId);

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    List<order_item> getItemsForOrderSync(long orderId);

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    void deleteByOrderId(long orderId);
}
