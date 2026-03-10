package com.example.silti;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "categories",
        foreignKeys = {
                @ForeignKey(
                        entity = table_category.class,
                        parentColumns = "id",
                        childColumns = "parentId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index("parentId")}
)


public class table_category {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    private Integer parentId;

    private String image;

    private Integer position;

    private Boolean isActive;

    private boolean isSystem;

    private long createdAt;

    public table_category(int id, String name, Integer parentId, String image, Integer position, Boolean isActive, boolean isSystem, long createdAt) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.image = image;
        this.position = position;
        this.isActive = isActive;
        this.isSystem = isSystem;
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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
