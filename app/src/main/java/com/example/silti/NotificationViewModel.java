package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

public class NotificationViewModel extends AndroidViewModel {
    private NotificationRepository notificationRepository;
    private MutableLiveData<Long> currentUserId = new MutableLiveData<>();

    public NotificationViewModel(Application application) {
        super(application);
        notificationRepository = new NotificationRepository(application);
    }

    public void setCurrentUserId(long userId) {
        currentUserId.setValue(userId);
    }

    // All notifications
    public LiveData<List<table_notifications>> getAllNotifications() {
        return Transformations.switchMap(currentUserId,
                userId -> notificationRepository.getNotificationsByUser(userId));
    }

    public LiveData<List<table_notifications>> getNotificationsByUser(long userId) {
        return notificationRepository.getNotificationsByUser(userId);
    }

    // Unread notifications
    public LiveData<List<table_notifications>> getUnreadNotifications() {
        return Transformations.switchMap(currentUserId,
                userId -> notificationRepository.getUnreadNotifications(userId));
    }

    public LiveData<List<table_notifications>> getUnreadNotifications(long userId) {
        return notificationRepository.getUnreadNotifications(userId);
    }

    // Unread count
    public LiveData<Integer> getUnreadCount() {
        return Transformations.switchMap(currentUserId,
                userId -> notificationRepository.getUnreadCount(userId));
    }

    public LiveData<Integer> getUnreadCount(long userId) {
        return notificationRepository.getUnreadCount(userId);
    }

    // Notifications by type
    public LiveData<List<table_notifications>> getNotificationsByType(String type) {
        return Transformations.switchMap(currentUserId,
                userId -> notificationRepository.getNotificationsByType(userId, type));
    }

    public LiveData<List<table_notifications>> getNotificationsByType(long userId, String type) {
        return notificationRepository.getNotificationsByType(userId, type);
    }

    // Insert
    public void insert(table_notifications notification) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            notification.setUserId(userId);
            notificationRepository.insert(notification);
        }
    }

    public void insertNotification(String title, String message,
                                   String type, String icon, Long relatedId) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            notificationRepository.insertNotification(userId, title, message, type, icon, relatedId);
        }
    }

    public void insertAll(List<table_notifications> notifications) {
        notificationRepository.insertAll(notifications);
    }

    // Mark as read
    public void markAsRead(long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    public void markAllAsRead() {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            notificationRepository.markAllAsRead(userId);
        }
    }

    // Update
    public void update(table_notifications notification) {
        notificationRepository.update(notification);
    }

    // Delete
    public void delete(table_notifications notification) {
        notificationRepository.delete(notification);
    }

    public void deleteById(long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public void deleteAllNotifications() {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            notificationRepository.deleteAllByUser(userId);
        }
    }

    public void deleteOldNotifications(long daysAgo) {
        notificationRepository.deleteOldNotifications(daysAgo);
    }

    // Create system notification
    public void createSystemNotification(String title, String message, String type) {
        insertNotification(title, message, type, "system", null);
    }

    // Create order notification
    public void createOrderNotification(String message, long orderId) {
        insertNotification("Order Update", message, "order", "shopping_cart", orderId);
    }

    // Create promo notification
    public void createPromoNotification(String title, String message) {
        insertNotification(title, message, "promo", "local_offer", null);
    }
}
