package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationRepository {
    private NotificationDao notificationDao;
    private ExecutorService executorService;

    public NotificationRepository(Application application) {
        AppDataBase db = AppDataBase.getInstance(application);
        notificationDao = db.notificationDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Insert
    public void insert(table_notifications notification) {
        executorService.execute(() -> notificationDao.insert(notification));
    }

    public void insertAll(List<table_notifications> notifications) {
        executorService.execute(() -> notificationDao.insertAll(notifications));
    }

    // Insert with parameters
    public void insertNotification(long userId, String title, String message,
                                   String type, String icon, Long relatedId) {
        executorService.execute(() -> {
            table_notifications notification = new table_notifications(
                    userId, title, message, type, icon, relatedId
            );
            notificationDao.insert(notification);
        });
    }

    // Update
    public void update(table_notifications notification) {
        executorService.execute(() -> notificationDao.update(notification));
    }

    public void markAsRead(long notificationId) {
        executorService.execute(() -> notificationDao.markAsRead(notificationId));
    }

    public void markAllAsRead(long userId) {
        executorService.execute(() -> notificationDao.markAllAsRead(userId));
    }

    // Delete
    public void delete(table_notifications notification) {
        executorService.execute(() -> notificationDao.deleteById(notification.getId()));
    }

    public void deleteById(long notificationId) {
        executorService.execute(() -> notificationDao.deleteById(notificationId));
    }

    public void deleteAllByUser(long userId) {
        executorService.execute(() -> notificationDao.deleteAllByUser(userId));
    }

    public void deleteOldNotifications(long daysAgo) {
        long timestamp = System.currentTimeMillis() - (daysAgo * 24 * 60 * 60 * 1000);
        executorService.execute(() -> notificationDao.deleteOldNotifications(timestamp));
    }

    // Read
    public LiveData<List<table_notifications>> getNotificationsByUser(long userId) {
        return notificationDao.getNotificationsByUser(userId);
    }

    public LiveData<List<table_notifications>> getUnreadNotifications(long userId) {
        return notificationDao.getUnreadNotifications(userId);
    }

    public LiveData<Integer> getUnreadCount(long userId) {
        return notificationDao.getUnreadCount(userId);
    }


    public LiveData<List<table_notifications>> getNotificationsByType(long userId, String type) {
        return notificationDao.getNotificationsByType(userId, type);
    }
}
