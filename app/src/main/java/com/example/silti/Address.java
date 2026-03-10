package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "addresses",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("userId")}
)
public class Address {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "userId")
    public long userId;

    @ColumnInfo(name = "fullName")
    public String fullName;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "city")
    public String city;

    @ColumnInfo(name = "street")
    public String street;

    @ColumnInfo(name = "details")
    public String details;

    @ColumnInfo(name = "landmark")
    public String landmark;

    @ColumnInfo(name = "addressType")
    public String addressType;

    @ColumnInfo(name = "isDefault")
    public boolean isDefault;

    @ColumnInfo(name = "createdAt")
    public long createdAt;

    // ✅ المُنشئ الافتراضي لـ Room
    public Address() {
        this.createdAt = System.currentTimeMillis();
        this.isDefault = false;
    }

    // ✅ منشئ مع معاملات مع @Ignore
    @Ignore
    public Address(long userId, String fullName, String phone, String city,
                   String street, String details, String addressType) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.city = city;
        this.street = street;
        this.details = details;
        this.addressType = addressType;
        this.isDefault = false;
        this.createdAt = System.currentTimeMillis();
    }
}