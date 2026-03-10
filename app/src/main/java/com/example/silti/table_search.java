package com.example.silti;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "searches",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("userId")}
)
public class table_search {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "userId")
    private long userId;

    @ColumnInfo(name = "query")
    private String query;

    @ColumnInfo(name = "searchedAt")
    private long searchedAt;

    @ColumnInfo(name = "resultCount")
    private int resultCount;

    // ✅ المُنشئ الافتراضي لـ Room
    public table_search() {
        this.searchedAt = System.currentTimeMillis();
    }

    // ✅ منشئ مع معاملات مع @Ignore
    @Ignore
    public table_search(long userId, String query, int resultCount) {
        this.userId = userId;
        this.query = query;
        this.resultCount = resultCount;
        this.searchedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public long getSearchedAt() { return searchedAt; }
    public void setSearchedAt(long searchedAt) { this.searchedAt = searchedAt; }

    public int getResultCount() { return resultCount; }
    public void setResultCount(int resultCount) { this.resultCount = resultCount; }
}