package com.learningapp.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history")
public class HistoryEntry {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String topic;
    public int scorePercent;   // e.g. 85 means 85%
    public long timestamp;     // System.currentTimeMillis()

    public HistoryEntry(String topic, int scorePercent, long timestamp) {
        this.topic = topic;
        this.scorePercent = scorePercent;
        this.timestamp = timestamp;
    }
}