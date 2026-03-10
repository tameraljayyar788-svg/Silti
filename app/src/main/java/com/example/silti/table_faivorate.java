package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "favorites",
        foreignKeys = {
                @ForeignKey(
                        entity = table_product.class,
                        parentColumns = "id",
                        childColumns = "productId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"productId", "userId"}, unique = true),
                @Index("userId")  // ✅ إضافة Index للـ userId
        }
)
public class table_faivorate {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "userId")
    private long userId;

    @ColumnInfo(name = "productId")
    private long productId;

    @ColumnInfo(name = "addedAt")
    private long addedAt;

    // ✅ المُنشئ الافتراضي لـ Room
    public table_faivorate() {
        this.addedAt = System.currentTimeMillis();
    }

    // ✅ منشئ مع معاملات مع @Ignore
    @Ignore
    public table_faivorate(long userId, long productId) {
        this.userId = userId;
        this.productId = productId;
        this.addedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public long getAddedAt() { return addedAt; }
    public void setAddedAt(long addedAt) { this.addedAt = addedAt; }
}