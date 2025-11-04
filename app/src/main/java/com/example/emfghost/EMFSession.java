package com.example.emfghost;

import java.util.List;

public class EMFSession {
    private String sessionId;
    private long startTime;
    private long endTime;
    private List<EMFReading> readings;

    public EMFSession() {
        // Default constructor required for Firebase
    }

    public EMFSession(String sessionId, long startTime, long endTime, List<EMFReading> readings) {
        this.sessionId = sessionId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.readings = readings;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public List<EMFReading> getReadings() {
        return readings;
    }

    public void setReadings(List<EMFReading> readings) {
        this.readings = readings;
    }
}

