package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "first_categories")

public class table_firstCategory {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "icon")
    public String icon;

    @ColumnInfo(name = "position")
    public int position;

    @ColumnInfo(name = "isActive")
    public boolean isActive;

    @ColumnInfo(name = "createdAt")
    public long createdAt;

    // Constructor


    public table_firstCategory(int id, String name, String icon, int position, boolean isActive, long createdAt) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.position = position;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}