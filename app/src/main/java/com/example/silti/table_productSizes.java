package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "product_sizes",
        foreignKeys = {
                @ForeignKey(
                        entity = table_product.class,
                        parentColumns = "id",
                        childColumns = "productId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = table_sizes.class,
                        parentColumns = "id",
                        childColumns = "sizeId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index("productId"), @Index("sizeId")}
)
public class table_productSizes {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "productId")
    public long productId;

    @ColumnInfo(name = "sizeId")
    public int sizeId;

    @ColumnInfo(name = "quantity")
    public int quantity;

    // ✅ المُنشئ الافتراضي لـ Room
    public table_productSizes() {}

    // ✅ منشئ مع معاملات مع @Ignore
    @Ignore
    public table_productSizes(long productId, int sizeId, int quantity) {
        this.productId = productId;
        this.sizeId = sizeId;
        this.quantity = quantity;
    }
}