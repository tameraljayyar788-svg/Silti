package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(
        tableName = "payment_methods",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("userId")
)
public class table_paymentMethode {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "userId")
    private long userId;

    @ColumnInfo(name = "methodType")
    private String methodType;  // visa, cash, etc.

    @ColumnInfo(name = "cardNumber")
    private String cardNumber;  // آخر 4 أرقام فقط

    @ColumnInfo(name = "cardHolderName")
    private String cardHolderName;

    @ColumnInfo(name = "expiryDate")
    private String expiryDate;

    @ColumnInfo(name = "isDefault")
    private boolean isDefault;

    @ColumnInfo(name = "createdAt")
    private long createdAt;

    // Constructors
    public table_paymentMethode() {
        this.createdAt = System.currentTimeMillis();
        this.isDefault = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getMethodType() { return methodType; }
    public void setMethodType(String methodType) { this.methodType = methodType; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}