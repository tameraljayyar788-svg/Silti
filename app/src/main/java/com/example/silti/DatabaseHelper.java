package com.example.silti;

import android.content.Context;

public class DatabaseHelper {
    private static DatabaseHelper instance;
    private AppDataBase database;
    private Context context;

    private DatabaseHelper(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDataBase.getInstance(this.context);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    public AppDataBase getDatabase() {
        return database;
    }

    // ============= Clear all tables =============
    public void clearAllTables() {
        AppDataBase.databaseWriteExecutor.execute(() -> {
            database.clearAllTables();
        });
    }

    // ============= Run in transaction =============
    public void runInTransaction(Runnable runnable) {
        database.runInTransaction(runnable);
    }

    // ============= Check if database is open =============
    public boolean isOpen() {
        return database != null && database.isOpen();
    }

    // ============= Close database =============
    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}

