package com.example.emfghost;

public interface SensorDataListener {
    void onMagneticFieldChanged(float magneticField);
    void onTemperatureChanged(float temperature);
    void onRecordingStateChanged(boolean isRecording);
    void onFlashlightStatusChanged(boolean isOn, float flickerFrequency);
}

