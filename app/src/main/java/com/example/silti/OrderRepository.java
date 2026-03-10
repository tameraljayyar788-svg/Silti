package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderRepository {
    private OrderDao orderDao;
    private OrderItemDao orderItemDao;
    private ExecutorService executorService;

    public OrderRepository(Application application) {
        AppDataBase db = AppDataBase.getInstance(application);
        orderDao = db.orderDao();
        orderItemDao = db.orderItemDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Insert with items
    public void insertOrder(table_order order, List<order_item> items) {
        executorService.execute(() -> {
            // Generate order number
            order.setOrderNumber(generateOrderNumber());

            long orderId = orderDao.insertOrder(order);

            for (order_item item : items) {
                item.setOrderId(orderId);
                orderItemDao.insertOrderItem(item);
            }
        });
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    // Update
    public void updateOrder(table_order order) {
        executorService.execute(() -> {
            order.setUpdatedAt(System.currentTimeMillis());
            orderDao.updateOrder(order);
        });
    }

    public void updateOrderStatus(long orderId, String status) {
        executorService.execute(() -> {
            table_order order = orderDao.getOrderById(orderId).getValue();
            if (order != null) {
                order.setOrderStatus(status);
                order.setUpdatedAt(System.currentTimeMillis());
                orderDao.updateOrder(order);
            }
        });
    }

    public void updatePaymentStatus(long orderId, String status) {
        executorService.execute(() -> {
            table_order order = orderDao.getOrderById(orderId).getValue();
            if (order != null) {
                order.setPaymentStatus(status);
                order.setUpdatedAt(System.currentTimeMillis());
                orderDao.updateOrder(order);
            }
        });
    }

    // Read
    public LiveData<List<table_order>> getAllOrders() {
        return orderDao.getAllOrders();
    }

    public LiveData<table_order> getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }

    public LiveData<List<table_order>> getOrdersByStatus(String status) {
        return orderDao.getOrdersByStatus(status);
    }

    // Order Items
    public LiveData<List<order_item>> getItemsForOrder(long orderId) {
        return orderItemDao.getItemsForOrder(orderId);
    }

    public void getItemsForOrderSync(long orderId, OrderItemsCallback callback) {
        executorService.execute(() -> {
            List<order_item> items = orderItemDao.getItemsForOrderSync(orderId);
            callback.onResult(items);
        });
    }

    // Callback
    public void getOrderCountByStatus(String status, CountCallback callback) {
        executorService.execute(() -> {
            int count = orderDao.getOrderCountByStatus(status);
            callback.onResult(count);
        });
    }

    public interface OrderItemsCallback { void onResult(List<order_item> items); }
    public interface CountCallback { void onResult(int count); }
}
