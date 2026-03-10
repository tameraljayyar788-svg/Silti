package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchRepository {
    private SearchDao searchDao;
    private ExecutorService executorService;

    public SearchRepository(Application application) {
        AppDataBase db = AppDataBase.getInstance(application);
        searchDao = db.searchDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Insert
    public void insertSearch(table_search search) {
        executorService.execute(() -> searchDao.insert(search));
    }

    public void saveSearch(long userId, String query, int resultCount) {
        executorService.execute(() -> {
            table_search search = new table_search(userId, query, resultCount);
            searchDao.insert(search);
        });
    }

    // Delete
    public void deleteSearchQuery(long userId, String query) {
        executorService.execute(() -> searchDao.deleteSearchQuery(userId, query));
    }

    public void deleteAllByUser(long userId) {
        executorService.execute(() -> searchDao.deleteAllByUser(userId));
    }

    public void deleteOldSearches(long daysAgo) {
        long timestamp = System.currentTimeMillis() - (daysAgo * 24 * 60 * 60 * 1000);
        executorService.execute(() -> searchDao.deleteOldSearches(timestamp));
    }

    // Read
    public LiveData<List<table_search>> getRecentSearches(long userId) {
        return searchDao.getRecentSearches(userId);
    }

    public LiveData<List<table_search>> searchInHistory(long userId, String query) {
        return searchDao.searchInHistory(userId, query);
    }

    public LiveData<List<String>> getTopSearches(long userId) {
        return searchDao.getTopSearches(userId);
    }

    // Callback
    public void getSearchCount(long userId, CountCallback callback) {
        executorService.execute(() -> {
            int count = searchDao.getSearchCount(userId);
            callback.onResult(count);
        });
    }

    public interface CountCallback { void onResult(int count); }
}