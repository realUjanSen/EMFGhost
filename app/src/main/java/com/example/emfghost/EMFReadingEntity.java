package com.example.emfghost;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "readings")
public class EMFReadingEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String sessionId;
    public long timestamp;
    public double magneticField;
    public double temperature;

    public EMFReadingEntity(String sessionId, long timestamp, double magneticField, double temperature) {
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.magneticField = magneticField;
        this.temperature = temperature;
    }
}

