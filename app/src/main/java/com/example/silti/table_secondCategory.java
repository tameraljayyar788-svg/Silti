package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "second_categories",
        foreignKeys = @ForeignKey(
                entity = table_firstCategory.class,  // ✅ تأكد من هذا السطر
                parentColumns = "id",
                childColumns = "firstCategoryId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("firstCategoryId")}  // ✅ تأكد من وجود الـ Index
)
public class table_secondCategory {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "firstCategoryId")
    public int firstCategoryId;

    @ColumnInfo(name = "icon")
    public String icon;

    @ColumnInfo(name = "isActive")
    public boolean isActive = true;

    @ColumnInfo(name = "createdAt")
    public long createdAt = System.currentTimeMillis();

    // Constructor بدون معامل - هذا هو المُنشئ الذي سيستخدمه Room
    public table_secondCategory() {}

    // ✅ أضف @Ignore للمنشئ الذي يحتوي معاملات
    @androidx.room.Ignore
    public table_secondCategory(String name, int firstCategoryId, String icon) {
        this.name = name;
        this.firstCategoryId = firstCategoryId;
        this.icon = icon;
    }
}
