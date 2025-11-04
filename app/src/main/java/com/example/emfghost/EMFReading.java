package com.example.emfghost;

public class EMFReading {
    private long timestamp;
    private double magneticField;
    private double temperature;

    public EMFReading() {
        // Default constructor required for Firebase
    }

    public EMFReading(long timestamp, double magneticField, double temperature) {
        this.timestamp = timestamp;
        this.magneticField = magneticField;
        this.temperature = temperature;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getMagneticField() {
        return magneticField;
    }

    public void setMagneticField(double magneticField) {
        this.magneticField = magneticField;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}

