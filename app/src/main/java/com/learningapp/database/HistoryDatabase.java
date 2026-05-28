package com.learningapp.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {HistoryEntry.class}, version = 1, exportSchema = false)
public abstract class HistoryDatabase extends RoomDatabase {
    private static HistoryDatabase instance;

    public abstract HistoryDao historyDao();

    public static synchronized HistoryDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            HistoryDatabase.class, "history_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}