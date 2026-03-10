package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VisaDao {
    @Insert
    long insert(table_visa visa);

    @Update
    void update(table_visa visa);

    @Delete
    void delete(table_visa visa);

    @Query("SELECT * FROM visa_cards WHERE userId = :userId ORDER BY isDefault DESC, createdAt DESC")
    LiveData<List<table_visa>> getVisasByUser(long userId);

    @Query("SELECT * FROM visa_cards WHERE userId = :userId AND cardType = :cardType")
    LiveData<List<table_visa>> getVisasByType(long userId, String cardType);

    @Query("SELECT * FROM visa_cards WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    LiveData<table_visa> getDefaultVisa(long userId);

    @Query("UPDATE visa_cards SET isDefault = 0 WHERE userId = :userId")
    void resetDefaultVisa(long userId);

    @Query("UPDATE visa_cards SET isDefault = 1 WHERE id = :cardId AND userId = :userId")
    void setAsDefault(int cardId, long userId);

    @Query("DELETE FROM visa_cards WHERE userId = :userId")
    void deleteAllByUser(long userId);

    @Query("DELETE FROM visa_cards WHERE id = :cardId")
    void deleteById(int cardId);

    @Query("SELECT * FROM visa_cards WHERE lastFourDigits = :lastFourDigits AND userId = :userId")
    LiveData<table_visa> getVisaByLastDigits(long userId, String lastFourDigits);
}
