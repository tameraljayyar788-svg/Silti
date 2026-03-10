package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VisaRepository {
    private VisaDao visaDao;
    private ExecutorService executorService;

    public VisaRepository(Application application) {
        AppDataBase db = AppDataBase.getInstance(application);
        visaDao = db.visaDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Insert
    public void insert(table_visa visa) {
        executorService.execute(() -> {
            if (visa.isDefault()) {
                visaDao.resetDefaultVisa(visa.getUserId());
            }
            visaDao.insert(visa);
        });
    }

    // Update
    public void update(table_visa visa) {
        executorService.execute(() -> {
            if (visa.isDefault()) {
                visaDao.resetDefaultVisa(visa.getUserId());
            }
            visaDao.update(visa);
        });
    }

    // Delete
    public void delete(table_visa visa) {
        executorService.execute(() -> visaDao.delete(visa));
    }

    public void deleteById(int cardId) {
        executorService.execute(() -> visaDao.deleteById(cardId));
    }

    public void deleteAllByUser(long userId) {
        executorService.execute(() -> visaDao.deleteAllByUser(userId));
    }

    // Set as default
    public void setAsDefault(int cardId, long userId) {
        executorService.execute(() -> {
            visaDao.resetDefaultVisa(userId);
            visaDao.setAsDefault(cardId, userId);
        });
    }

    // Read
    public LiveData<List<table_visa>> getVisasByUser(long userId) {
        return visaDao.getVisasByUser(userId);
    }

    public LiveData<List<table_visa>> getVisasByType(long userId, String cardType) {
        return visaDao.getVisasByType(userId, cardType);
    }

    public LiveData<table_visa> getDefaultVisa(long userId) {
        return visaDao.getDefaultVisa(userId);
    }

    public LiveData<table_visa> getVisaByLastDigits(long userId, String lastFourDigits) {
        return visaDao.getVisaByLastDigits(userId, lastFourDigits);
    }
}