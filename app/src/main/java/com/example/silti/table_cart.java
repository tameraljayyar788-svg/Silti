package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "cart",
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
        indices = {@Index("productId"), @Index("userId")}
)
public class table_cart {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "userId")
    private long userId;

    @ColumnInfo(name = "productId")
    private long productId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "image")
    private String image;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "size")
    private String size;

    @ColumnInfo(name = "addedAt")
    private long addedAt;

    // ✅ المُنشئ الافتراضي لـ Room
    public table_cart() {
        this.addedAt = System.currentTimeMillis();
    }

    // ✅ منشئ مع معاملات مع @Ignore
    @Ignore
    public table_cart(long userId, long productId, String name, double price,
                      String image, int quantity, String size) {
        this.userId = userId;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.image = image;
        this.quantity = quantity;
        this.size = size;
        this.addedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public long getAddedAt() { return addedAt; }
    public void setAddedAt(long addedAt) { this.addedAt = addedAt; }
}
