package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "visa_cards",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("userId")
)
public class table_visa {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "userId")
    private long userId;

    @ColumnInfo(name = "cardNumber")
    private String cardNumber;  // مشفر

    @ColumnInfo(name = "cardHolderName")
    private String cardHolderName;

    @ColumnInfo(name = "expiryMonth")
    private String expiryMonth;

    @ColumnInfo(name = "expiryYear")
    private String expiryYear;

    @ColumnInfo(name = "cvv")
    private String cvv;  // مشفر

    @ColumnInfo(name = "cardType")
    private String cardType;  // Visa, MasterCard, etc.

    @ColumnInfo(name = "lastFourDigits")
    private String lastFourDigits;

    @ColumnInfo(name = "isDefault")
    private boolean isDefault;

    @ColumnInfo(name = "createdAt")
    private long createdAt;

    // Constructors
    public table_visa() {
        this.createdAt = System.currentTimeMillis();
        this.isDefault = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }

    public String getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }

    public String getExpiryYear() { return expiryYear; }
    public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getLastFourDigits() { return lastFourDigits; }
    public void setLastFourDigits(String lastFourDigits) { this.lastFourDigits = lastFourDigits; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
