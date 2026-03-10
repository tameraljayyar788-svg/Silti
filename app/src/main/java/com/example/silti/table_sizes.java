package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "sizes")
public class table_sizes {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "sizeName")
    public String sizeName;

    @ColumnInfo(name = "category")
    public String category;

    // ✅ المُنشئ الافتراضي لـ Room
    public table_sizes() {}

    // ✅ منشئ مع معاملات مع @Ignore
    @Ignore
    public table_sizes(String sizeName, String category) {
        this.sizeName = sizeName;
        this.category = category;
    }
}
