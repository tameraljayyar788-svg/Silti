package com.example.silti;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private LiveData<List<User>> allUsers;
    private MutableLiveData<Long> currentUserId = new MutableLiveData<>();

    public UserViewModel(Application application) {
        super(application);
        userRepository = new UserRepository(application);
        allUsers = userRepository.getAllUsers();
    }

    public void setCurrentUser(Long userId) {
        currentUserId.setValue(userId);
    }

    public LiveData<Long> getCurrentUserId() {
        return currentUserId;
    }

    // Insert
    public void insert(User user) {
        userRepository.insert(user);
    }

    // Update
    public void update(User user) {
        userRepository.update(user);
    }

    // Delete
    public void delete(User user) {
        userRepository.delete(user);
    }

    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }

    // Read
    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public LiveData<User> getUserById(Long id) {
        return userRepository.getUserById(id);
    }

    public LiveData<List<User>> getAdminUsers() {
        return userRepository.getAdminUsers();
    }

    // Authentication
    public void loginUser(String email, String password, UserRepository.LoginCallback callback) {
        userRepository.loginUser(email, password, callback);
    }

    public void getUserByEmail(String email, UserRepository.UserCallback callback) {
        userRepository.getUserByEmail(email, callback);
    }

    public void isEmailExists(String email, UserRepository.EmailCheckCallback callback) {
        userRepository.isEmailExists(email, callback);
    }

    // Profile
    public void updateProfileImage(Long userId, String imagePath) {
        userRepository.updateProfileImage(userId, imagePath);
    }

    public void updatePassword(Long userId, String newPassword) {
        userRepository.updatePassword(userId, newPassword);
    }

    // Current user methods
    public void updateCurrentUserProfile(String imagePath) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            updateProfileImage(userId, imagePath);
        }
    }

    public void updateCurrentUserPassword(String newPassword) {
        Long userId = currentUserId.getValue();
        if (userId != null) {
            updatePassword(userId, newPassword);
        }
    }
}
