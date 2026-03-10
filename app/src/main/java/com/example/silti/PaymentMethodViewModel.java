package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class PaymentMethodViewModel extends AndroidViewModel {
    private PaymentMethodRepository paymentMethodRepository;
    private MutableLiveData<Long> currentUserId = new MutableLiveData<>();
    private LiveData<List<table_paymentMethode>> userPaymentMethods;

    public PaymentMethodViewModel(Application application) {
        super(application);
        paymentMethodRepository = new PaymentMethodRepository(application);
    }

    public void setCurrentUserId(long userId) {
        currentUserId.setValue(userId);
        userPaymentMethods = paymentMethodRepository.getMethodsByUser(userId);
    }

    // Get payment methods for current user
    public LiveData<List<table_paymentMethode>> getUserPaymentMethods() {
        return userPaymentMethods;
    }

    public LiveData<List<table_paymentMethode>> getMethodsByUser(long userId) {
        return paymentMethodRepository.getMethodsByUser(userId);
    }

    public LiveData<List<table_paymentMethode>> getMethodsByType(String methodType) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            return paymentMethodRepository.getMethodsByType(userId, methodType);
        }
        return null;
    }

    public LiveData<table_paymentMethode> getDefaultMethod() {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            return paymentMethodRepository.getDefaultMethod(userId);
        }
        return null;
    }

    // Insert
    public void insert(table_paymentMethode method) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            method.setUserId(userId);
            paymentMethodRepository.insert(method);
        }
    }

    public void insert(String methodType, String cardNumber, String cardHolderName,
                       String expiryDate, boolean isDefault) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            table_paymentMethode method = new table_paymentMethode();
            method.setUserId(userId);
            method.setMethodType(methodType);
            method.setCardNumber(cardNumber);
            method.setCardHolderName(cardHolderName);
            method.setExpiryDate(expiryDate);
            method.setDefault(isDefault);
            paymentMethodRepository.insert(method);
        }
    }

    // Update
    public void update(table_paymentMethode method) {
        paymentMethodRepository.update(method);
    }

    // Set as default
    public void setAsDefault(int methodId) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            paymentMethodRepository.setAsDefault(methodId, userId);
        }
    }

    // Delete
    public void delete(table_paymentMethode method) {
        paymentMethodRepository.delete(method);
    }

    public void deleteById(int methodId) {
        paymentMethodRepository.deleteById(methodId);
    }

    public void deleteAllMethods() {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            paymentMethodRepository.deleteAllByUser(userId);
        }
    }
}
