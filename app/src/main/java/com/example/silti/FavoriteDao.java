package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert
    long insertFavorite(table_faivorate favorite);

    @Delete
    void deleteFavorite(table_faivorate favorite);

    @Query("DELETE FROM favorites WHERE userId = :userId")
    void clearAllFavorites(long userId);

    @Query("SELECT * FROM favorites WHERE userId = :userId AND productId = :productId LIMIT 1")
    table_faivorate getFavoriteByProductId(long userId, long productId);

    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY addedAt DESC")
    LiveData<List<table_faivorate>> getAllFavorites(long userId);

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId AND productId = :productId")
    int isFavorite(long userId, long productId);

    @Query("DELETE FROM favorites WHERE userId = :userId AND productId = :productId")
    void removeFromFavorites(long userId, long productId);

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId")
    LiveData<Integer> getFavoritesCount(long userId);


}

