package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartRepository {
    private CartDao cartDao;
    private ExecutorService executorService;

    public CartRepository(Application application) {
        AppDataBase db = AppDataBase.getInstance(application);
        cartDao = db.cartDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Insert
    public void insertCartItem(table_cart cartItem) {
        executorService.execute(() -> {
            table_cart existing = cartDao.getCartItemByProductId(cartItem.getUserId(), cartItem.getProductId());
            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + cartItem.getQuantity());
                cartDao.updateCartItem(existing);
            } else {
                cartDao.insertCartItem(cartItem);
            }
        });
    }

    public void addToCart(long userId, long productId, String name, double price,
                          String image, int quantity, String size) {
        executorService.execute(() -> {
            table_cart existing = cartDao.getCartItemByProductId(userId, productId);
            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + quantity);
                cartDao.updateCartItem(existing);
            } else {
                table_cart newItem = new table_cart(userId, productId, name, price, image, quantity, size);
                cartDao.insertCartItem(newItem);
            }
        });
    }

    // Update
    public void updateCartItem(table_cart cartItem) {
        executorService.execute(() -> cartDao.updateCartItem(cartItem));
    }

    public void deleteAllByUser(long userId) {
        executorService.execute(() -> cartDao.clearCart(userId));  // clearCart موجودة بالفعل
    }

    public void updateQuantity(int cartItemId, int quantity) {
        executorService.execute(() -> cartDao.updateQuantity(cartItemId, quantity));
    }

    // Delete
    public void deleteCartItem(table_cart cartItem) {
        executorService.execute(() -> cartDao.deleteCartItem(cartItem));
    }

    public void removeFromCart(long userId, long productId) {
        executorService.execute(() -> cartDao.removeFromCart(userId, productId));
    }

    public void clearCart(long userId) {
        executorService.execute(() -> cartDao.clearCart(userId));
    }

    // Read
    public LiveData<List<table_cart>> getCartItemsByUser(long userId) {
        return cartDao.getCartItemsByUser(userId);
    }

    public LiveData<Integer> getCartItemCount(long userId) {
        return cartDao.getCartItemCount(userId);
    }

    public LiveData<Double> getCartTotalPrice(long userId) {
        return cartDao.getCartTotalPrice(userId);
    }

    // Callback
    public void isInCart(long userId, long productId, CartCheckCallback callback) {
        executorService.execute(() -> {
            boolean exists = cartDao.isInCart(userId, productId);
            callback.onResult(exists);
        });
    }

    public interface CartCheckCallback { void onResult(boolean exists); }
}
