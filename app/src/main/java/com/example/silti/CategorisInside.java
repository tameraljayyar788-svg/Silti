package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(
        tableName = "table_CategorisInside",
        foreignKeys = @ForeignKey(
                entity = table_secondCategory.class,  // ✅ تم التصحيح
                parentColumns = "id",
                childColumns = "secondCategoryId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("secondCategoryId")
)public class CategorisInside {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "SecondCategory")
    public int SecondCategory;
}
