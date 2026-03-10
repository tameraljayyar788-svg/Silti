package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderItemRepository {
    private OrderItemDao orderItemDao;
    private ExecutorService executorService;

    public OrderItemRepository(Application application) {
        AppDataBase db = AppDataBase.getInstance(application);
        orderItemDao = db.orderItemDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Insert
    public void insertOrderItem(order_item item) {
        executorService.execute(() -> orderItemDao.insertOrderItem(item));
    }

    public void insertAllOrderItems(List<order_item> items) {
        executorService.execute(() -> orderItemDao.insertAllOrderItems(items));
    }

    // Read
    public LiveData<List<order_item>> getItemsForOrder(long orderId) {
        return orderItemDao.getItemsForOrder(orderId);
    }

    public void getItemsForOrderSync(long orderId, OrderItemsCallback callback) {
        executorService.execute(() -> {
            List<order_item> items = orderItemDao.getItemsForOrderSync(orderId);
            callback.onResult(items);
        });
    }

    // Delete
    public void deleteByOrderId(long orderId) {
        executorService.execute(() -> orderItemDao.deleteByOrderId(orderId));
    }

    public interface OrderItemsCallback { void onResult(List<order_item> items); }
}
