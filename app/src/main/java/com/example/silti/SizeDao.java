package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SizeDao {
    @Insert
    long insert(table_sizes size);

    @Query("SELECT * FROM sizes ORDER BY sizeName")
    LiveData<List<table_sizes>> getAllSizes();

    @Query("SELECT * FROM sizes WHERE id = :category ORDER BY sizeName")
    LiveData<List<table_sizes>> getSizesByCategory(String category);

    @Query("SELECT * FROM sizes WHERE sizeName = :sizeName LIMIT 1")
    LiveData<table_sizes> getSizeByName(String sizeName);
}

