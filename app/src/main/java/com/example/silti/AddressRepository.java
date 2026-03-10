package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddressRepository {
    private AddressDao dao;
    private ExecutorService executorService;

    public AddressRepository(Application application) {
        AppDataBase db = AppDataBase.getInstance(application);
        dao = db.addressDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Address address) {
        executorService.execute(() -> {
            if (address.isDefault) {
                dao.resetDefaultAddress(address.userId);
            }
            dao.insert(address);
        });
    }

    public void update(Address address) {
        executorService.execute(() -> {
            if (address.isDefault) {
                dao.resetDefaultAddress(address.userId);
            }
            dao.update(address);
        });
    }

    public void delete(Address address) {
        executorService.execute(() -> dao.delete(address));
    }

    public void deleteById(int addressId) {
        executorService.execute(() -> dao.deleteById(addressId));
    }

    public void setAsDefault(int addressId, long userId) {
        executorService.execute(() -> {
            dao.resetDefaultAddress(userId);
            dao.setAsDefault(addressId, userId);
        });
    }

    public LiveData<List<Address>> getAddressesByUser(long userId) {
        return dao.getAddressesByUser(userId);
    }

    public LiveData<Address> getAddressById(int addressId) {
        return dao.getAddressById(addressId);
    }
    public void deleteAllByUser(long userId) {
        executorService.execute(() -> dao.deleteAllByUser(userId));
    }

    public LiveData<Address> getDefaultAddress(long userId) {
        return dao.getDefaultAddress(userId);
    }

    public void getAddressCount(long userId, CountCallback callback) {
        executorService.execute(() -> {
            int count = dao.getAddressCount(userId);
            callback.onResult(count);
        });
    }

    public interface CountCallback {
        void onResult(int count);
    }
}
