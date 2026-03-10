package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FirstCategoryDao {
    @Insert
    long insert(table_firstCategory category);

    @Update
    void update(table_firstCategory category);

    @Delete
    void delete(table_firstCategory category);

    @Query("SELECT * FROM first_categories WHERE isActive = 1 ORDER BY position ASC")
    LiveData<List<table_firstCategory>> getAllActiveCategories();

    @Query("SELECT * FROM first_categories ORDER BY position ASC")
    LiveData<List<table_firstCategory>> getAllCategories();

    @Query("SELECT * FROM first_categories WHERE id = :categoryId")
    LiveData<table_firstCategory> getCategoryById(int categoryId);

    @Query("SELECT * FROM first_categories WHERE name LIKE '%' || :query || '%'")
    LiveData<List<table_firstCategory>> searchCategories(String query);

    @Query("UPDATE first_categories SET isActive = :isActive WHERE id = :categoryId")
    void updateCategoryStatus(int categoryId, boolean isActive);

    @Query("UPDATE first_categories SET position = :position WHERE id = :categoryId")
    void updatePosition(int categoryId, int position);

    @Query("DELETE FROM first_categories WHERE id = :categoryId")
    void deleteById(int categoryId);

    @Query("SELECT MAX(position) FROM first_categories")
    int getMaxPosition();
}
