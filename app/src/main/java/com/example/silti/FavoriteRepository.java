package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteRepository {
    private FavoriteDao favoriteDao;
    private ExecutorService executorService;

    public FavoriteRepository(Application application) {
        AppDataBase db = AppDataBase.getInstance(application);
        favoriteDao = db.favoriteDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Insert
    public void insertFavorite(table_faivorate favorite) {
        executorService.execute(() -> favoriteDao.insertFavorite(favorite));
    }

    public void addToFavorite(long userId, long productId) {
        executorService.execute(() -> {
            if (favoriteDao.isFavorite(userId, productId) == 0) {
                table_faivorate favorite = new table_faivorate(userId, productId);
                favoriteDao.insertFavorite(favorite);
            }
        });
    }

    // Delete
    public void deleteFavorite(table_faivorate favorite) {
        executorService.execute(() -> favoriteDao.deleteFavorite(favorite));
    }

    public void removeFromFavorite(long userId, long productId) {
        executorService.execute(() -> favoriteDao.removeFromFavorites(userId, productId));
    }

    public void clearAllFavorites(long userId) {
        executorService.execute(() -> favoriteDao.clearAllFavorites(userId));
    }

    // Read
    public LiveData<List<table_faivorate>> getAllFavorites(long userId) {
        return favoriteDao.getAllFavorites(userId);
    }

    public LiveData<Integer> getFavoritesCount(long userId) {
        return favoriteDao.getFavoritesCount(userId);
    }
    public void deleteAllByUser(long userId) {
        executorService.execute(() -> favoriteDao.clearAllFavorites(userId));  // clearAllFavorites موجودة
    }
    // Callback
    public void isFavorite(long userId, long productId, FavoriteCheckCallback callback) {
        executorService.execute(() -> {
            int count = favoriteDao.isFavorite(userId, productId);
            callback.onResult(count > 0);
        });
    }

    public void toggleFavorite(long userId, long productId) {
        executorService.execute(() -> {
            if (favoriteDao.isFavorite(userId, productId) > 0) {
                favoriteDao.removeFromFavorites(userId, productId);
            } else {
                table_faivorate favorite = new table_faivorate(userId, productId);
                favoriteDao.insertFavorite(favorite);
            }
        });
    }

    public interface FavoriteCheckCallback { void onResult(boolean isFavorite); }
}
