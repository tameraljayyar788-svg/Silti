package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private CartRepository cartRepository;
    private MutableLiveData<Long> currentUserId = new MutableLiveData<>();
    private LiveData<List<table_cart>> cartItems;
    private LiveData<Integer> cartCount;
    private LiveData<Double> cartTotal;

    public CartViewModel(Application application) {
        super(application);
        cartRepository = new CartRepository(application);
    }

    public void setCurrentUserId(long userId) {
        currentUserId.setValue(userId);
        cartItems = cartRepository.getCartItemsByUser(userId);
        cartCount = cartRepository.getCartItemCount(userId);
        cartTotal = cartRepository.getCartTotalPrice(userId);
    }

    public LiveData<List<table_cart>> getCartItems() {
        return cartItems;
    }

    public LiveData<Integer> getCartCount() {
        return cartCount;
    }

    public LiveData<Double> getCartTotal() {
        return cartTotal;
    }

    public void addToCart(table_cart cartItem) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            cartItem.setUserId(userId);
            cartRepository.insertCartItem(cartItem);
        }
    }

    public void addToCart(long productId, String name, double price,
                          String image, int quantity, String size) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            cartRepository.addToCart(userId, productId, name, price, image, quantity, size);
        }
    }

    public void updateCartItem(table_cart cartItem) {
        cartRepository.updateCartItem(cartItem);
    }

    public void updateQuantity(int cartItemId, int quantity) {
        cartRepository.updateQuantity(cartItemId, quantity);
    }

    public void removeFromCart(long productId) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            cartRepository.removeFromCart(userId, productId);
        }
    }

    public void deleteCartItem(table_cart cartItem) {
        cartRepository.deleteCartItem(cartItem);
    }

    public void clearCart() {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            cartRepository.clearCart(userId);
        }
    }

    public void isInCart(long productId, CartRepository.CartCheckCallback callback) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            cartRepository.isInCart(userId, productId, callback);
        }
    }

    public String getFormattedTotal() {
        Double total = cartTotal.getValue();
        if (total != null) {
            return String.format("$%.2f", total);
        }
        return "$0.00";
    }
}
