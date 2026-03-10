package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

public class OrderItemViewModel extends AndroidViewModel {
    private OrderItemRepository orderItemRepository;
    private MutableLiveData<Long> currentOrderId = new MutableLiveData<>();

    public OrderItemViewModel(Application application) {
        super(application);
        orderItemRepository = new OrderItemRepository(application);
    }

    public void setCurrentOrderId(long orderId) {
        currentOrderId.setValue(orderId);
    }

    // Get items for current order
    public LiveData<List<order_item>> getItemsForCurrentOrder() {
        return Transformations.switchMap(currentOrderId,
                id -> orderItemRepository.getItemsForOrder(id));
    }

    // Get items for specific order
    public LiveData<List<order_item>> getItemsForOrder(long orderId) {
        return orderItemRepository.getItemsForOrder(orderId);
    }

    // Get items synchronously with callback
    public void getItemsForOrderSync(long orderId, OrderItemRepository.OrderItemsCallback callback) {
        orderItemRepository.getItemsForOrderSync(orderId, callback);
    }

    // Insert
    public void insertOrderItem(order_item item) {
        orderItemRepository.insertOrderItem(item);
    }

    public void insertAllOrderItems(List<order_item> items) {
        orderItemRepository.insertAllOrderItems(items);
    }

    // Delete
    public void deleteByOrderId(long orderId) {
        orderItemRepository.deleteByOrderId(orderId);
    }

    public void deleteCurrentOrderItems() {
        Long orderId = currentOrderId.getValue();
        if (orderId != null) {
            orderItemRepository.deleteByOrderId(orderId);
        }
    }

    // Calculate total for items
    public double calculateItemsTotal(List<order_item> items) {
        double total = 0;
        if (items != null) {
            for (order_item item : items) {
                total += item.getPrice() * item.getQuantity();
            }
        }
        return total;
    }
}
