package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SearchDao {
    @Insert
    long insert(table_search search);

    @Query("SELECT * FROM searches WHERE userId = :userId ORDER BY searchedAt DESC LIMIT 10")
    LiveData<List<table_search>> getRecentSearches(long userId);

    @Query("SELECT * FROM searches WHERE userId = :userId AND query LIKE '%' || :query || '%' ORDER BY searchedAt DESC")
    LiveData<List<table_search>> searchInHistory(long userId, String query);

    @Query("DELETE FROM searches WHERE userId = :userId")
    void deleteAllByUser(long userId);

    @Query("DELETE FROM searches WHERE userId = :userId AND query = :query")
    void deleteSearchQuery(long userId, String query);

    @Query("DELETE FROM searches WHERE searchedAt < :timestamp")
    void deleteOldSearches(long timestamp);

    @Query("SELECT COUNT(*) FROM searches WHERE userId = :userId")
    int getSearchCount(long userId);

    @Query("SELECT query FROM searches WHERE userId = :userId GROUP BY query ORDER BY COUNT(*) DESC LIMIT 5")
    LiveData<List<String>> getTopSearches(long userId);
}

