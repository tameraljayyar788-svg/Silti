package com.example.silti;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
        private UserDao userDao;
        private LiveData<List<User>> allUsers;
        private ExecutorService executorService;

        public UserRepository(Application application) {
            AppDataBase db = AppDataBase.getInstance(application);
            userDao = db.userDao();
            allUsers = userDao.getAllUsers();
            executorService = Executors.newSingleThreadExecutor();
        }

        public void insert(User user) {
            executorService.execute(() -> userDao.insert(user));
        }

        public void update(User user) {
            executorService.execute(() -> userDao.update(user));
        }

        public void delete(User user) {
            executorService.execute(() -> userDao.delete(user));
        }

        public LiveData<List<User>> getAllUsers() {
            return allUsers;
        }

        public LiveData<User> getUserById(Long id) {
            return userDao.getUserById(id);
        }

        public LiveData<List<User>> getAdminUsers() {
            return userDao.getAdminUsers();
        }

        public void loginUser(String email, String password, LoginCallback callback) {
            executorService.execute(() -> {
                User user = userDao.loginUser(email, password);
                callback.onResult(user);
            });
        }

        public void getUserByEmail(String email, UserCallback callback) {
            executorService.execute(() -> {
                User user = userDao.getUserByEmail(email);
                callback.onResult(user);
            });
        }

        public void isEmailExists(String email, EmailCheckCallback callback) {
            executorService.execute(() -> {
                int count = userDao.isEmailExists(email);
                callback.onResult(count > 0);
            });
        }

        public void updateProfileImage(Long userId, String imagePath) {
            executorService.execute(() -> userDao.updateProfileImage(userId, imagePath));
        }

        public void updatePassword(Long userId, String newPassword) {
            executorService.execute(() -> userDao.updatePassword(userId, newPassword));
        }

        public void deleteById(Long userId) {
            executorService.execute(() -> userDao.deleteById(userId));
        }

        public interface LoginCallback { void onResult(User user); }
        public interface UserCallback { void onResult(User user); }
        public interface EmailCheckCallback { void onResult(boolean exists); }
    }
