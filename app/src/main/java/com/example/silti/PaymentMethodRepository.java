package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaymentMethodRepository {
        private PaymentMethodDao paymentMethodDao;
        private ExecutorService executorService;

        public PaymentMethodRepository(Application application) {
            AppDataBase db = AppDataBase.getInstance(application);
            paymentMethodDao = db.paymentMethodDao();
            executorService = Executors.newSingleThreadExecutor();
        }

        // Insert
        public void insert(table_paymentMethode method) {
            executorService.execute(() -> {
                if (method.isDefault()) {
                    paymentMethodDao.resetDefaultMethod(method.getUserId());
                }
                paymentMethodDao.insert(method);
            });
        }

        // Update
        public void update(table_paymentMethode method) {
            executorService.execute(() -> {
                if (method.isDefault()) {
                    paymentMethodDao.resetDefaultMethod(method.getUserId());
                }
                paymentMethodDao.update(method);
            });
        }

        // Delete
        public void delete(table_paymentMethode method) {
            executorService.execute(() -> paymentMethodDao.delete(method));
        }

        public void deleteById(int methodId) {
            executorService.execute(() -> paymentMethodDao.deleteById(methodId));
        }

        public void deleteAllByUser(long userId) {
            executorService.execute(() -> paymentMethodDao.deleteAllByUser(userId));
        }


        // Set as default
        public void setAsDefault(int methodId, long userId) {
            executorService.execute(() -> {
                paymentMethodDao.resetDefaultMethod(userId);
                paymentMethodDao.setAsDefault(methodId, userId);
            });
        }

        // Read
        public LiveData<List<table_paymentMethode>> getMethodsByUser(long userId) {
            return paymentMethodDao.getMethodsByUser(userId);
        }

        public LiveData<List<table_paymentMethode>> getMethodsByType(long userId, String methodType) {
            return paymentMethodDao.getMethodsByType(userId, methodType);
        }

        public LiveData<table_paymentMethode> getDefaultMethod(long userId) {
            return paymentMethodDao.getDefaultMethod(userId);
        }
    }
