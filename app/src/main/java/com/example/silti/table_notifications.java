package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "notifications",
        foreignKeys = @ForeignKey(
                entity = User.class,
        parentColumns = "id",
        childColumns = "userId",
        onDelete = ForeignKey.CASCADE
),
indices = {@Index("userId")}
        )
public class table_notifications {
    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "userId")
    private Long userId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "timestamp")
    private Long timestamp;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "icon")
    private String icon;

    @ColumnInfo(name = "isRead")
    private boolean isRead;

    @ColumnInfo(name = "relatedId")
    private Long relatedId;

    // ✅ المُنشئ الافتراضي لـ Room
    public table_notifications() {
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    // ✅ منشئ مع معاملات مع @Ignore
    @Ignore
    public table_notifications(Long userId, String title, String message,
                               String type, String icon, Long relatedId) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.icon = icon;
        this.relatedId = relatedId;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }
}
