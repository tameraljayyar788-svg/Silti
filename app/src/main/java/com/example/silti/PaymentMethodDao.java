package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PaymentMethodDao {
    @Insert
    long insert(table_paymentMethode method);

    @Update
    void update(table_paymentMethode method);

    @Delete
    void delete(table_paymentMethode method);

    @Query("SELECT * FROM payment_methods WHERE userId = :userId ORDER BY isDefault DESC, createdAt DESC")
    LiveData<List<table_paymentMethode>> getMethodsByUser(long userId);

    @Query("SELECT * FROM payment_methods WHERE userId = :userId AND methodType = :methodType")
    LiveData<List<table_paymentMethode>> getMethodsByType(long userId, String methodType);

    @Query("SELECT * FROM payment_methods WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    LiveData<table_paymentMethode> getDefaultMethod(long userId);

    @Query("UPDATE payment_methods SET isDefault = 0 WHERE userId = :userId")
    void resetDefaultMethod(long userId);

    @Query("UPDATE payment_methods SET isDefault = 1 WHERE id = :methodId AND userId = :userId")
    void setAsDefault(int methodId, long userId);

    @Query("DELETE FROM payment_methods WHERE userId = :userId")
    void deleteAllByUser(long userId);

    @Query("DELETE FROM payment_methods WHERE id = :methodId")
    void deleteById(int methodId);
}

