package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    long insertCategory(table_category category);

    @Update
    void updateCategory(table_category category);

    @Delete
    void deleteCategory(table_category category);

    @Query("SELECT * FROM categories WHERE parentId IS NULL")
    LiveData<List<table_category>> getMainCategories();

    @Query("SELECT * FROM categories WHERE parentId = :parentId")
    LiveData<List<table_category>> getSubCategories(int parentId);

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    LiveData<table_category> getCategoryById(int categoryId);

    @Query("SELECT * FROM categories WHERE isSystem = 0 AND isActive = 1")
    LiveData<List<table_category>> getAllActiveCategories();
}