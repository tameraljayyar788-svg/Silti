package com.example.silti;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseRepository {
    protected final ExecutorService executorService;

    public BaseRepository() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    protected AppDataBase getDatabase(Application application) {
        return AppDataBase.getInstance(application);
    }
}
