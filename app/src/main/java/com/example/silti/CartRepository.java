package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartRepository {
    private CartDao dao;
    private ExecutorService executorService;

    public CartRepository(Application application) {
        AppDataBase db = AppDataBase.getInstance(application);
        dao = db.cartDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insertCartItem(table_cart cartItem) {
        executorService.execute(() -> {
            table_cart existing = dao.getCartItemByProductId(cartItem.getUserId(), cartItem.getProductId());
            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + cartItem.getQuantity());
                dao.updateCartItem(existing);
            } else {
                dao.insertCartItem(cartItem);
            }
        });
    }

    public void addToCart(long userId, long productId, String name, double price,
                          String image, int quantity, String size) {
        executorService.execute(() -> {
            table_cart existing = dao.getCartItemByProductId(userId, productId);
            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + quantity);
                dao.updateCartItem(existing);
            } else {
                table_cart newItem = new table_cart(userId, productId, name, price, image, quantity, size);
                dao.insertCartItem(newItem);
            }
        });
    }

    public void updateCartItem(table_cart cartItem) {
        executorService.execute(() -> dao.updateCartItem(cartItem));
    }

    public void updateQuantity(int cartItemId, int quantity) {
        executorService.execute(() -> dao.updateQuantity(cartItemId, quantity));
    }

    public void deleteCartItem(table_cart cartItem) {
        executorService.execute(() -> dao.deleteCartItem(cartItem));
    }

    public void removeFromCart(long userId, long productId) {
        executorService.execute(() -> dao.removeFromCart(userId, productId));
    }

    public void clearCart(long userId) {
        executorService.execute(() -> dao.clearCart(userId));
    }

    public LiveData<List<table_cart>> getCartItemsByUser(long userId) {
        return dao.getCartItemsByUser(userId);
    }

    public LiveData<Integer> getCartItemCount(long userId) {
        return dao.getCartItemCount(userId);
    }

    public LiveData<Double> getCartTotalPrice(long userId) {
        return dao.getCartTotalPrice(userId);
    }

    public void isInCart(long userId, long productId, CartCheckCallback callback) {
        executorService.execute(() -> {
            boolean exists = dao.isInCart(userId, productId);
            callback.onResult(exists);
        });
    }

    public interface CartCheckCallback {
        void onResult(boolean exists);
    }
}
