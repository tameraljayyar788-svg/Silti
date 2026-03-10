package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sizes")
public class Sizes {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "sizeName")
    public String sizeName;
}
