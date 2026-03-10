package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SecondCategoryDao {
    @Insert
    long insert(table_secondCategory category);

    @Update
    void update(table_secondCategory category);

    @Delete
    void delete(table_secondCategory category);

    @Query("SELECT * FROM second_categories WHERE firstCategoryId = :firstCategoryId AND isActive = 1")
    LiveData<List<table_secondCategory>> getCategoriesByFirstCategory(int firstCategoryId);

    @Query("SELECT * FROM second_categories WHERE id = :categoryId")
    LiveData<table_secondCategory> getCategoryById(int categoryId);

    @Query("SELECT * FROM second_categories WHERE firstCategoryId = :firstCategoryId")
    LiveData<List<table_secondCategory>> getAllCategoriesByFirstCategory(int firstCategoryId);

    @Query("UPDATE second_categories SET isActive = :isActive WHERE id = :categoryId")
    void updateCategoryStatus(int categoryId, boolean isActive);

    @Query("DELETE FROM second_categories WHERE firstCategoryId = :firstCategoryId")
    void deleteByFirstCategory(int firstCategoryId);

    @Query("DELETE FROM second_categories WHERE id = :categoryId")
    void deleteById(int categoryId);

    @Query("SELECT COUNT(*) FROM second_categories WHERE firstCategoryId = :firstCategoryId AND isActive = 1")
    int getActiveCountByFirstCategory(int firstCategoryId);
}
