package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class AddressViewModel extends AndroidViewModel {
    private AddressRepository addressRepository;
    private MutableLiveData<Long> currentUserId = new MutableLiveData<>();
    private LiveData<List<Address>> userAddresses;

    public AddressViewModel(Application application) {
        super(application);
        addressRepository = new AddressRepository(application);
    }

    public void setCurrentUserId(long userId) {
        currentUserId.setValue(userId);
        userAddresses = addressRepository.getAddressesByUser(userId);
    }

    // Get addresses for current user
    public LiveData<List<Address>> getUserAddresses() {
        return userAddresses;
    }

    // Insert
    public void insert(Address address) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            address.userId = userId;
            addressRepository.insert(address);
        }
    }

    // Update
    public void update(Address address) {
        addressRepository.update(address);
    }

    // Delete
    public void delete(Address address) {
        addressRepository.delete(address);
    }

    public void deleteById(int addressId) {
        addressRepository.deleteById(addressId);
    }

    public void deleteAllAddresses() {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            addressRepository.deleteAllByUser(userId);
        }
    }
    public void setAsDefault(int addressId) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            addressRepository.setAsDefault(addressId, userId);
        }
    }

    // Get single address
    public LiveData<Address> getAddressById(int addressId) {
        return addressRepository.getAddressById(addressId);
    }

    public LiveData<Address> getDefaultAddress() {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            return addressRepository.getDefaultAddress(userId);
        }
        return null;
    }

    // Address count
    public void getAddressCount(AddressRepository.CountCallback callback) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            addressRepository.getAddressCount(userId, callback);
        }
    }

    // Create new address
    public void createNewAddress(String fullName, String phone, String city,
                                 String street, String details, String addressType) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            Address address = new Address(userId, fullName, phone, city,
                    street, details, addressType);
            insert(address);
        }
    }
}
