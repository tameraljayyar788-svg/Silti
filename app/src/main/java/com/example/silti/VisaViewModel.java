package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class VisaViewModel extends AndroidViewModel {
    private VisaRepository visaRepository;
    private MutableLiveData<Long> currentUserId = new MutableLiveData<>();
    private LiveData<List<table_visa>> userVisas;

    public VisaViewModel(Application application) {
        super(application);
        visaRepository = new VisaRepository(application);
    }

    public void setCurrentUserId(long userId) {
        currentUserId.setValue(userId);
        userVisas = visaRepository.getVisasByUser(userId);
    }

    // Get visas for current user
    public LiveData<List<table_visa>> getUserVisas() {
        return userVisas;
    }

    public LiveData<List<table_visa>> getVisasByUser(long userId) {
        return visaRepository.getVisasByUser(userId);
    }

    public LiveData<List<table_visa>> getVisasByType(String cardType) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            return visaRepository.getVisasByType(userId, cardType);
        }
        return null;
    }

    public LiveData<table_visa> getDefaultVisa() {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            return visaRepository.getDefaultVisa(userId);
        }
        return null;
    }

    public LiveData<table_visa> getVisaByLastDigits(String lastFourDigits) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            return visaRepository.getVisaByLastDigits(userId, lastFourDigits);
        }
        return null;
    }

    // Insert
    public void insert(table_visa visa) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            visa.setUserId(userId);
            visaRepository.insert(visa);
        }
    }

    public void insert(String cardNumber, String cardHolderName, String expiryMonth,
                       String expiryYear, String cvv, String cardType,
                       String lastFourDigits, boolean isDefault) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            table_visa visa = new table_visa();
            visa.setUserId(userId);
            visa.setCardNumber(cardNumber);
            visa.setCardHolderName(cardHolderName);
            visa.setExpiryMonth(expiryMonth);
            visa.setExpiryYear(expiryYear);
            visa.setCvv(cvv);
            visa.setCardType(cardType);
            visa.setLastFourDigits(lastFourDigits);
            visa.setDefault(isDefault);
            visaRepository.insert(visa);
        }
    }

    // Update
    public void update(table_visa visa) {
        visaRepository.update(visa);
    }

    // Set as default
    public void setAsDefault(int cardId) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            visaRepository.setAsDefault(cardId, userId);
        }
    }

    // Delete
    public void delete(table_visa visa) {
        visaRepository.delete(visa);
    }

    public void deleteById(int cardId) {
        visaRepository.deleteById(cardId);
    }

    public void deleteAllVisas() {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            visaRepository.deleteAllByUser(userId);
        }
    }

    // Helper method to mask card number
    public String maskCardNumber(String cardNumber) {
        if (cardNumber != null && cardNumber.length() >= 4) {
            return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
        }
        return cardNumber;
    }
}
