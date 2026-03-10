package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "table_CategorisInside",
        foreignKeys = @ForeignKey(
                entity = table_secondCategory.class,
                parentColumns = "id",
                childColumns = "secondCategoryId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("secondCategoryId")}
)
public class table_CategorisInside {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "secondCategoryId")
    public int secondCategoryId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "icon")
    public String icon;

    @ColumnInfo(name = "position")
    public int position;

    @ColumnInfo(name = "isActive")
    public boolean isActive = true;

    @ColumnInfo(name = "createdAt")
    public long createdAt = System.currentTimeMillis();

    // ✅ المُنشئ الافتراضي لـ Room
    public table_CategorisInside() {}

    // ✅ منشئ مع معاملات مع @Ignore
    @Ignore
    public table_CategorisInside(int secondCategoryId, String name, String icon, int position) {
        this.secondCategoryId = secondCategoryId;
        this.name = name;
        this.icon = icon;
        this.position = position;
    }
}