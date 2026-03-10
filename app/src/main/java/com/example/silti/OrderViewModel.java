package com.example.silti;


import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

public class OrderViewModel extends AddressViewModel {
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private MutableLiveData<Long> currentUserId = new MutableLiveData<>();
    private MutableLiveData<Long> currentOrderId = new MutableLiveData<>();

    public OrderViewModel(Application application) {
        super(application);
        orderRepository = new OrderRepository(application);
        orderItemRepository = new OrderItemRepository(application);
    }

    public void setCurrentUserId(long userId) {
        currentUserId.setValue(userId);
    }

    public void setCurrentOrderId(long orderId) {
        currentOrderId.setValue(orderId);
    }

    // All orders
    public LiveData<List<table_order>> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    // Order by ID
    public LiveData<table_order> getOrderById(long orderId) {
        return orderRepository.getOrderById(orderId);
    }

    public LiveData<table_order> getCurrentOrder() {
        return Transformations.switchMap(currentOrderId,
                id -> orderRepository.getOrderById(id));
    }

    // Orders by status
    public LiveData<List<table_order>> getOrdersByStatus(String status) {
        return orderRepository.getOrdersByStatus(status);
    }

    // Create new order
    public void createOrder(table_order order, List<order_item> items) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            order.setUserId(userId);
            orderRepository.insertOrder(order, items);
        }
    }

    // Update order
    public void updateOrder(table_order order) {
        orderRepository.updateOrder(order);
    }

    public void updateOrderStatus(long orderId, String status) {
        orderRepository.updateOrderStatus(orderId, status);
    }

    public void updatePaymentStatus(long orderId, String status) {
        orderRepository.updatePaymentStatus(orderId, status);
    }

    // Order items
    public LiveData<List<order_item>> getItemsForOrder(long orderId) {
        return orderItemRepository.getItemsForOrder(orderId);
    }

    public LiveData<List<order_item>> getItemsForCurrentOrder() {
        return Transformations.switchMap(currentOrderId,
                id -> orderItemRepository.getItemsForOrder(id));
    }

    public void getItemsForOrderSync(long orderId, OrderRepository.OrderItemsCallback callback) {
        orderRepository.getItemsForOrderSync(orderId, callback);
    }

    // Order count
    public void getOrderCountByStatus(String status, OrderRepository.CountCallback callback) {
        orderRepository.getOrderCountByStatus(status, callback);
    }

    // Generate order number
    public String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
}
