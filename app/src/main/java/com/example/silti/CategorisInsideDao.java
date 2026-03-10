package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategorisInsideDao {
    @Insert
    long insert(table_CategorisInside category);

    @Update
    void update(table_CategorisInside category);

    @Delete
    void delete(table_CategorisInside category);

    @Query("SELECT * FROM table_CategorisInside WHERE secondCategoryId = :secondCategoryId AND isActive = 1 ORDER BY position ASC")
    LiveData<List<table_CategorisInside>> getCategoriesBySecondCategory(int secondCategoryId);

    @Query("SELECT * FROM table_CategorisInside WHERE id = :categoryId")
    LiveData<table_CategorisInside> getCategoryById(int categoryId);

    @Query("SELECT * FROM table_CategorisInside WHERE secondCategoryId = :secondCategoryId")
    LiveData<List<table_CategorisInside>> getAllCategoriesBySecondCategory(int secondCategoryId);

    @Query("UPDATE table_CategorisInside SET isActive = :isActive WHERE id = :categoryId")
    void updateCategoryStatus(int categoryId, boolean isActive);

    @Query("UPDATE table_CategorisInside SET position = :position WHERE id = :categoryId")
    void updatePosition(int categoryId, int position);

    @Query("DELETE FROM table_CategorisInside WHERE secondCategoryId = :secondCategoryId")
    void deleteBySecondCategory(int secondCategoryId);

    @Query("DELETE FROM table_CategorisInside WHERE id = :categoryId")
    void deleteById(int categoryId);

    @Query("SELECT COUNT(*) FROM table_CategorisInside WHERE secondCategoryId = :secondCategoryId AND isActive = 1")
    int getActiveCountBySecondCategory(int secondCategoryId);

    @Query("SELECT MAX(position) FROM table_CategorisInside WHERE secondCategoryId = :secondCategoryId")
    int getMaxPositionBySecondCategory(int secondCategoryId);
}
