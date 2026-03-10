package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "order_items",
        foreignKeys = {
                @ForeignKey(
                        entity = table_order.class,
                        parentColumns = "id",
                        childColumns = "orderId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = table_product.class,
                        parentColumns = "id",
                        childColumns = "productId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index("orderId"), @Index("productId")}
)
public class order_item {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "orderId")
    private long orderId;

    @ColumnInfo(name = "productId")
    private long productId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "discount")
    private double discount;

    @ColumnInfo(name = "image")
    private String image;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "size")
    private String size;

    // ✅ المُنشئ الافتراضي لـ Room
    public order_item() {}

    // ✅ منشئ مع معاملات مع @Ignore
    @Ignore
    public order_item(long orderId, long productId, String name, double price,
                      double discount, String image, int quantity, String size) {
        this.orderId = orderId;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.image = image;
        this.quantity = quantity;
        this.size = size;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getOrderId() { return orderId; }
    public void setOrderId(long orderId) { this.orderId = orderId; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
}