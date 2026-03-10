package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FirstCategory {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "nameFCategory")
    public String nameFCtegory;

    @ColumnInfo(name = "IconCategory")
    public String IconCategory;

}
