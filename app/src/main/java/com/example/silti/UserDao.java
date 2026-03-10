package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User loginUser(String email, String password);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    LiveData<User> getUserById(Long id);

    @Query("SELECT * FROM users WHERE isAdmin = 1")
    LiveData<List<User>> getAdminUsers();

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int isEmailExists(String email);

    @Query("UPDATE users SET imageProfile = :imagePath WHERE id = :userId")
    void updateProfileImage(Long userId, String imagePath);

    @Query("UPDATE users SET password = :newPassword WHERE id = :userId")
    void updatePassword(Long userId, String newPassword);

    @Query("DELETE FROM users WHERE id = :userId")
    void deleteById(Long userId);

    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();
}
