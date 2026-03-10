package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private SearchRepository searchRepository;
    private MutableLiveData<Long> currentUserId = new MutableLiveData<>();
    private MutableLiveData<String> currentSearchQuery = new MutableLiveData<>();

    public SearchViewModel(Application application) {
        super(application);
        searchRepository = new SearchRepository(application);
    }

    public void setCurrentUserId(long userId) {
        currentUserId.setValue(userId);
    }

    public void setCurrentSearchQuery(String query) {
        currentSearchQuery.setValue(query);
    }

    // Recent searches
    public LiveData<List<table_search>> getRecentSearches() {
        return Transformations.switchMap(currentUserId,
                userId -> searchRepository.getRecentSearches(userId));
    }

    public LiveData<List<table_search>> getRecentSearches(long userId) {
        return searchRepository.getRecentSearches(userId);
    }

    // Search in history
    public LiveData<List<table_search>> searchInHistory() {
        return Transformations.switchMap(currentUserId, userId ->
                Transformations.switchMap(currentSearchQuery, query ->
                        searchRepository.searchInHistory(userId, query)));
    }

    public LiveData<List<table_search>> searchInHistory(long userId, String query) {
        return searchRepository.searchInHistory(userId, query);
    }

    // Top searches
    public LiveData<List<String>> getTopSearches() {
        return Transformations.switchMap(currentUserId,
                userId -> searchRepository.getTopSearches(userId));
    }

    public LiveData<List<String>> getTopSearches(long userId) {
        return searchRepository.getTopSearches(userId);
    }

    // Save search
    public void saveSearch(String query, int resultCount) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            searchRepository.saveSearch(userId, query, resultCount);
        }
    }

    public void insertSearch(table_search search) {
        searchRepository.insertSearch(search);
    }

    // Delete
    public void deleteSearchQuery(String query) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            searchRepository.deleteSearchQuery(userId, query);
        }
    }

    public void deleteAllSearches() {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            searchRepository.deleteAllByUser(userId);
        }
    }

    public void deleteOldSearches(long daysAgo) {
        searchRepository.deleteOldSearches(daysAgo);
    }

    // Search count
    public void getSearchCount(SearchRepository.CountCallback callback) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            searchRepository.getSearchCount(userId, callback);
        }
    }
}
