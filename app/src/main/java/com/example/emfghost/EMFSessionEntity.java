package com.example.emfghost;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity(tableName = "sessions")
public class EMFSessionEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String sessionId;
    public long startTime;
    public long endTime;

    @TypeConverters(Converters.class)
    public List<EMFReading> readings;

    public EMFSessionEntity(String sessionId, long startTime, long endTime, List<EMFReading> readings) {
        this.sessionId = sessionId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.readings = readings;
    }
}

