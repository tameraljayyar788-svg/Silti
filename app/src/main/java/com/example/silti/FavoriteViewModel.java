package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class FavoriteViewModel extends AndroidViewModel {
    private FavoriteRepository favoriteRepository;
    private MutableLiveData<Long> currentUserId = new MutableLiveData<>();
    private LiveData<List<table_faivorate>> favoriteItems;
    private LiveData<Integer> favoritesCount;

    public FavoriteViewModel(Application application) {
        super(application);
        favoriteRepository = new FavoriteRepository(application);
    }

    public void setCurrentUserId(long userId) {
        currentUserId.setValue(userId);
        favoriteItems = favoriteRepository.getAllFavorites(userId);
        favoritesCount = favoriteRepository.getFavoritesCount(userId);
    }

    // Favorite data
    public LiveData<List<table_faivorate>> getFavoriteItems() {
        return favoriteItems;
    }

    public LiveData<Integer> getFavoritesCount() {
        return favoritesCount;
    }

    // Add to favorites
    public void addToFavorite(long productId) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            favoriteRepository.addToFavorite(userId, productId);
        }
    }

    // Remove from favorites
    public void removeFromFavorite(long productId) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            favoriteRepository.removeFromFavorite(userId, productId);
        }
    }

    // Toggle favorite
    public void toggleFavorite(long productId) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            favoriteRepository.toggleFavorite(userId, productId);
        }
    }

    // Clear all favorites
    public void clearAllFavorites() {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            favoriteRepository.clearAllFavorites(userId);
        }
    }

    // Check if product is favorite
    public void isFavorite(long productId, FavoriteRepository.FavoriteCheckCallback callback) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            favoriteRepository.isFavorite(userId, productId, callback);
        }
    }

    // Insert favorite directly
    public void insertFavorite(table_faivorate favorite) {
        favoriteRepository.insertFavorite(favorite);
    }

    // Delete favorite directly
    public void deleteFavorite(table_faivorate favorite) {
        favoriteRepository.deleteFavorite(favorite);
    }
    public void isFavorite(long userId, long productId, FavoriteRepository.FavoriteCheckCallback callback) {
        favoriteRepository.isFavorite(userId, productId, callback);
    }
}
