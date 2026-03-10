package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "products",
        foreignKeys = {
                @ForeignKey(
                        entity = table_firstCategory.class,  // ✅ تم التصحيح
                        parentColumns = "id",
                        childColumns = "firstCategoryId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = table_secondCategory.class,  // ✅ تم التصحيح
                        parentColumns = "id",
                        childColumns = "secondCategoryId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = table_CategorisInside.class,  // ✅ تم التصحيح
                        parentColumns = "id",
                        childColumns = "insideCategoryId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("firstCategoryId"),
                @Index("secondCategoryId"),
                @Index("insideCategoryId")
        }
)
public class table_product {


    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "discount")
    private double discount;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "image")
    private String image;

    @ColumnInfo(name = "images")
    private String images;  // صور متعددة مفصولة بفواصل

    @ColumnInfo(name = "firstCategoryId")
    private int firstCategoryId;

    @ColumnInfo(name = "secondCategoryId")
    private int secondCategoryId;

    @ColumnInfo(name = "insideCategoryId")
    private int insideCategoryId;

    @ColumnInfo(name = "rate")
    private int rate;

    @ColumnInfo(name = "soldCount")
    private int soldCount;

    @ColumnInfo(name = "brand")
    private String brand;

    @ColumnInfo(name = "isActive")
    private boolean isActive;

    @ColumnInfo(name = "isFeatured")
    private boolean isFeatured;

    @ColumnInfo(name = "createdAt")
    private long createdAt;

    // Constructor
    public table_product() {
        this.createdAt = System.currentTimeMillis();
        this.isActive = true;
        this.isFeatured = false;
        this.soldCount = 0;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public int getFirstCategoryId() { return firstCategoryId; }
    public void setFirstCategoryId(int firstCategoryId) { this.firstCategoryId = firstCategoryId; }

    public int getSecondCategoryId() { return secondCategoryId; }
    public void setSecondCategoryId(int secondCategoryId) { this.secondCategoryId = secondCategoryId; }

    public int getInsideCategoryId() { return insideCategoryId; }
    public void setInsideCategoryId(int insideCategoryId) { this.insideCategoryId = insideCategoryId; }

    public int getRate() { return rate; }
    public void setRate(int rate) { this.rate = rate; }

    public int getSoldCount() { return soldCount; }
    public void setSoldCount(int soldCount) { this.soldCount = soldCount; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    // Helper method
    public double getDiscountedPrice() {
        return price * (1 - discount / 100);
    }

}
