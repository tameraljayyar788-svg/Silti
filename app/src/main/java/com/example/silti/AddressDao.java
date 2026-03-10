package com.example.silti;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AddressDao {
    @Insert
    long insert(Address address);

    @Update
    void update(Address address);

    @Delete
    void delete(Address address);

    @Query("SELECT * FROM addresses WHERE userId = :userId ORDER BY isDefault DESC, createdAt DESC")
    LiveData<List<Address>> getAddressesByUser(long userId);

    @Query("SELECT * FROM addresses WHERE id = :addressId")
    LiveData<Address> getAddressById(int addressId);

    @Query("UPDATE addresses SET isDefault = 0 WHERE userId = :userId")
    void resetDefaultAddress(long userId);

    @Query("UPDATE addresses SET isDefault = 1 WHERE id = :addressId AND userId = :userId")
    void setAsDefault(int addressId, long userId);

    @Query("SELECT * FROM addresses WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    LiveData<Address> getDefaultAddress(long userId);

    @Query("DELETE FROM addresses WHERE id = :addressId")
    void deleteById(int addressId);

    @Query("DELETE FROM addresses WHERE userId = :userId")
    void deleteAllByUser(long userId);

    @Query("SELECT COUNT(*) FROM addresses WHERE userId = :userId")
    int getAddressCount(long userId);
}
