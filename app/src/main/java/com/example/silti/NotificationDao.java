package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NotificationDao {

    @Insert
    long insert(table_notifications notification);

    @Insert
    void insertAll(List<table_notifications> notifications);

    @Update
    void update(table_notifications notification);

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    LiveData<List<table_notifications>> getNotificationsByUser(long userId);

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY timestamp DESC")
    LiveData<List<table_notifications>> getUnreadNotifications(long userId);

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    void markAllAsRead(long userId);

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    void markAsRead(long notificationId);

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    LiveData<Integer> getUnreadCount(long userId);

    @Query("DELETE FROM notifications WHERE userId = :userId")
    void deleteAllByUser(long userId);

    @Query("DELETE FROM notifications WHERE id = :notificationId")
    void deleteById(long notificationId);

    @Query("DELETE FROM notifications WHERE timestamp < :timestamp")
    void deleteOldNotifications(long timestamp);

    @Query("SELECT * FROM notifications WHERE type = :type AND userId = :userId ORDER BY timestamp DESC")
    LiveData<List<table_notifications>> getNotificationsByType(long userId, String type);
}
