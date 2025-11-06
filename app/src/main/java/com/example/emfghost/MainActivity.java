package com.example.emfghost;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ViewPager2 viewPager;
    private MainPagerAdapter pagerAdapter;

    // Sensors
    private SensorManager sensorManager;
    private Sensor magneticSensor;
    private Sensor temperatureSensor;

    // Sensor info
    private String magneticSensorName = "Unknown";
    private String temperatureSensorName = "Simulated";
    private boolean isTemperatureSimulated = true;

    // Data
    private float currentMagneticField = 0f;
    private float currentTemperature = 20f;
    private boolean isRecording = false;
    private long recordingStartTime = 0;
    private List<EMFReading> currentSessionReadings;

    // Database
    private AppDatabase database;
    private Executor executor = Executors.newSingleThreadExecutor();

    // Sound
    private SoundPool soundPool;
    private int soundId;
    private int currentStreamId = -1;
    private float currentPlaybackRate = 1.0f;

    // Flashlight
    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashlightEnabled = false;
    private boolean isFlashlightOn = false;
    private float currentFlickerFrequency = 0f; // Dims per second
    private Handler flashHandler = new Handler(Looper.getMainLooper());
    private Runnable flickerRunnable;
    private int currentFlickerMode = -1; // Track current flicker mode to avoid restarting

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable recordingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_container);

        // Initialize ViewPager
        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new MainPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Initialize sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        // Get sensor names - show hardware model
        if (magneticSensor != null) {
            magneticSensorName = extractSensorIdentifier(magneticSensor);
        }

        if (temperatureSensor != null) {
            temperatureSensorName = extractSensorIdentifier(temperatureSensor);
            isTemperatureSimulated = false;
        } else {
            temperatureSensorName = "Environmental Simulation Module";
            isTemperatureSimulated = true;
            // Start with normal room temperature
            currentTemperature = 26.5f;
        }

        // Initialize database
        database = AppDatabase.getDatabase(this);

        // Initialize sound pool
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();
        soundId = soundPool.load(this, R.raw.emf_alert, 1);

        // Initialize camera for flashlight
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        currentSessionReadings = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (magneticSensor != null) {
            sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        stopSound();
        stopFlashlightFlicker();
        turnOffFlashlight();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        stopFlashlightFlicker();
        turnOffFlashlight();
        if (isRecording) {
            stopRecording();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            currentMagneticField = (float) Math.sqrt(x * x + y * y + z * z);

            // Simulate temperature drop when magnetic field is high
            if (isTemperatureSimulated) {
                simulateTemperature();
            }

            notifyFragments();
            updateSound();
            updateFlashlight();

        } else if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            currentTemperature = event.values[0];
            notifyFragments();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed
    }

    private void simulateTemperature() {
        // Normal temperature: 25-28°C with slow gradual changes
        // When magnetic field >= 60: temperature starts dropping gradually
        // At 100: reaches 0°C
        // At 200+: around -10°C (minimum)

        float targetTemp;

        if (currentMagneticField < 60) {
            // Normal range: 25-28°C with gentle oscillation
            long time = System.currentTimeMillis();
            double wave = Math.sin(time / 10000.0) * 1.5; // Slow wave
            targetTemp = 26.5f + (float)wave;
        } else {
            // Temperature drops gradually with magnetic field
            // Linear drop from 26°C at 60μT to 0°C at 100μT, then continues to -10°C at 200μT

            if (currentMagneticField <= 100) {
                // Linear interpolation from 26°C at 60μT to 0°C at 100μT
                // slope = (0 - 26) / (100 - 60) = -26/40 = -0.65
                targetTemp = 26f - ((currentMagneticField - 60f) * 0.65f);
            } else {
                // Below 0°C: continues dropping from 0°C at 100μT to -10°C at 200μT
                // slope = (-10 - 0) / (200 - 100) = -10/100 = -0.1
                targetTemp = 0f - ((currentMagneticField - 100f) * 0.1f);
                // Cap at -10°C minimum
                targetTemp = Math.max(-10f, targetTemp);
            }
        }

        // Smooth transition (lerp)
        float smoothing = 0.05f;
        currentTemperature += (targetTemp - currentTemperature) * smoothing;
    }

    private String extractSensorIdentifier(Sensor sensor) {
        // Try to get a meaningful hardware identifier
        String name = sensor.getName();
        String vendor = sensor.getVendor();

        // If name is null or empty
        if (name == null || name.isEmpty()) {
            return "Unknown Module";
        }

        // Check if name is generic (like "MAGNETOMETER" or "Magnetic Field Sensor")
        String upperName = name.toUpperCase();
        boolean isGeneric = upperName.equals("MAGNETOMETER") ||
                           upperName.equals("TEMPERATURE") ||
                           upperName.equals("MAGNETIC FIELD SENSOR") ||
                           upperName.equals("AMBIENT TEMPERATURE SENSOR") ||
                           upperName.equals("MAGNETIC FIELD") ||
                           upperName.equals("AMBIENT TEMPERATURE");

        if (isGeneric) {
            // Use vendor name and create identifier
            if (vendor != null && !vendor.isEmpty() && !vendor.equalsIgnoreCase("unknown")) {
                // Clean up vendor name
                String cleanVendor = vendor.replace("Inc.", "").replace("Co.", "").replace(",", "").trim();
                String[] vendorParts = cleanVendor.split(" ");
                String shortVendor = vendorParts[0];

                // Create identifier like "Qualcomm-MF1" or "MediaTek-MF2"
                String typeCode = sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD ? "MF" : "AT";
                return shortVendor + "-" + typeCode + sensor.getVersion();
            } else {
                // Fallback: use type and version
                int type = sensor.getType();
                String typePrefix = type == Sensor.TYPE_MAGNETIC_FIELD ? "MFD" : "TMP";
                return typePrefix + "-" + sensor.getVersion();
            }
        }

        // Name is specific (like "akm09919" or "AK09918 Magnetic field Sensor")
        // Extract just the chip model number
        String trimmedName = name.trim();

        // If it contains spaces, take the first part (usually the chip model)
        if (trimmedName.contains(" ")) {
            String[] parts = trimmedName.split(" ");
            return parts[0].toUpperCase(); // Return first part in uppercase (chip model)
        }

        // No spaces, return as-is but uppercase for consistency
        return trimmedName.toUpperCase();
    }

    private void notifyFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof SensorDataListener && fragment.isVisible()) {
                ((SensorDataListener) fragment).onMagneticFieldChanged(currentMagneticField);
                ((SensorDataListener) fragment).onTemperatureChanged(currentTemperature);
            }
        }
    }

    private void notifyRecordingState() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof SensorDataListener) {
                ((SensorDataListener) fragment).onRecordingStateChanged(isRecording);
            }
        }
    }

    // Public methods for fragments to get sensor info
    public String getMagneticSensorName() {
        return magneticSensorName;
    }

    public String getTemperatureSensorName() {
        return temperatureSensorName;
    }

    public boolean isTemperatureSimulated() {
        return isTemperatureSimulated;
    }

    private void updateSound() {
        if (currentMagneticField >= 60) {
            // Calculate playback rate (pitch) based on magnetic field
            // 60 μT = lowest pitch (0.5x), 600+ μT = highest pitch (2.0x)
            float pitch = 0.5f + ((currentMagneticField - 60) / 540f) * 1.5f;
            pitch = Math.max(0.5f, Math.min(2.0f, pitch));

            if (currentStreamId == -1) {
                // Start playing
                currentStreamId = soundPool.play(soundId, 1.0f, 1.0f, 1, -1, pitch);
                currentPlaybackRate = pitch;
            } else if (Math.abs(currentPlaybackRate - pitch) > 0.05f) {
                // Update pitch
                soundPool.setRate(currentStreamId, pitch);
                currentPlaybackRate = pitch;
            }
        } else {
            stopSound();
        }
    }

    private void stopSound() {
        if (currentStreamId != -1 && soundPool != null) {
            soundPool.stop(currentStreamId);
            currentStreamId = -1;
        }
    }

    public void toggleRecording() {
        if (isRecording) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    private void startRecording() {
        isRecording = true;
        recordingStartTime = System.currentTimeMillis();
        currentSessionReadings = new ArrayList<>();

        notifyRecordingState();

        // Record data every second
        recordingRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRecording) {
                    long timestamp = System.currentTimeMillis() - recordingStartTime;
                    EMFReading reading = new EMFReading(timestamp, currentMagneticField, currentTemperature);
                    currentSessionReadings.add(reading);
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(recordingRunnable);
    }

    private void stopRecording() {
        isRecording = false;
        handler.removeCallbacks(recordingRunnable);

        notifyRecordingState();

        // Save session to database
        if (!currentSessionReadings.isEmpty()) {
            long endTime = System.currentTimeMillis();
            String sessionId = "session_" + recordingStartTime;
            EMFSessionEntity session = new EMFSessionEntity(sessionId, recordingStartTime, endTime, currentSessionReadings);

            executor.execute(() -> database.sessionDao().insert(session));
        }

        currentSessionReadings = new ArrayList<>();
    }

    // Flashlight methods
    public void setFlashlightEnabled(boolean enabled) {
        android.util.Log.d("EMFGhost", "setFlashlightEnabled called: " + enabled);
        isFlashlightEnabled = enabled;
        if (!enabled) {
            android.util.Log.d("EMFGhost", "Disabling flashlight");
            currentFlickerMode = -1; // Reset mode when disabling
            currentFlickerFrequency = 0f; // Reset frequency
            stopFlashlightFlicker();
            turnOffFlashlight();
        } else {
            android.util.Log.d("EMFGhost", "Enabling flashlight, calling updateFlashlight()");
            currentFlickerMode = -1; // Reset mode when enabling to force initial setup
            updateFlashlight();
        }
    }

    private void updateFlashlight() {
        android.util.Log.d("EMFGhost", "updateFlashlight() called. isFlashlightEnabled=" + isFlashlightEnabled + ", currentMagneticField=" + currentMagneticField);

        if (!isFlashlightEnabled) {
            android.util.Log.d("EMFGhost", "Flashlight disabled, returning");
            return;
        }

        // Determine which flicker mode we should be in
        int newFlickerMode;
        if (currentMagneticField < 60) {
            newFlickerMode = 0; // Steady on
        } else if (currentMagneticField < 100) {
            newFlickerMode = 1; // Little flicker
        } else if (currentMagneticField < 200) {
            newFlickerMode = 2; // Moderate flicker
        } else if (currentMagneticField < 400) {
            newFlickerMode = 3; // Fast flicker
        } else {
            newFlickerMode = 4; // Extreme flicker
        }

        android.util.Log.d("EMFGhost", "Calculated flicker mode: " + newFlickerMode + " (current mode: " + currentFlickerMode + ")");

        // Only restart if mode changed
        if (newFlickerMode != currentFlickerMode) {
            android.util.Log.d("EMFGhost", "Flicker mode changing: " + currentFlickerMode + " -> " + newFlickerMode + " (EMF: " + currentMagneticField + ")");
            currentFlickerMode = newFlickerMode;

            // Stop any existing flicker pattern
            stopFlashlightFlicker();

            switch (currentFlickerMode) {
                case 0: // Below 60: steady on
                    android.util.Log.d("EMFGhost", "Starting steady ON mode");
                    currentFlickerFrequency = 0f;
                    turnOnFlashlight();
                    break;
                case 1: // 60-100: little flicker
                    android.util.Log.d("EMFGhost", "Starting little flicker mode");
                    currentFlickerFrequency = 1.0f;
                    startLittleFlicker();
                    break;
                case 2: // 100-200: moderate flicker
                    android.util.Log.d("EMFGhost", "Starting moderate flicker mode");
                    currentFlickerFrequency = 1.5f;
                    startModerateFlicker();
                    break;
                case 3: // 200-400: fast flicker
                    android.util.Log.d("EMFGhost", "Starting fast flicker mode");
                    currentFlickerFrequency = 2.5f;
                    startFastFlicker();
                    break;
                case 4: // 400+: extreme flicker
                    android.util.Log.d("EMFGhost", "Starting extreme flicker mode");
                    currentFlickerFrequency = 4.0f;
                    startExtremeFlicker();
                    break;
            }
        } else {
            android.util.Log.d("EMFGhost", "Mode unchanged, no restart needed");
        }
    }

    private void startLittleFlicker() {
         // 60-100: Gentle flicker - 1 flicker per second
        // ON for 0.7s, OFF for 0.3s (noticeable but gentle)
        flickerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isFlashlightEnabled || currentFlickerMode != 1) {
                    return;
                }

                // Turn ON for 0.7s
                turnOnFlashlight();

                // Turn OFF after 0.7s
                flashHandler.postDelayed(() -> {
                    if (isFlashlightEnabled && currentFlickerMode == 1) {
                        turnOffFlashlight();
                    }
                }, 700);

                // Repeat every 1 second
                flashHandler.postDelayed(this, 1000);
            }
        };

        // Start first flicker immediately
        flashHandler.post(flickerRunnable);
    }

    private void startModerateFlicker() {
        // 100-200: Moderate flicker - 1.5 flickers per second
        // Each burst: 0.4s ON, 0.27s OFF (total ~0.67s per burst)
        flickerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isFlashlightEnabled || currentFlickerMode != 2) {
                    return;
                }

                // Turn ON for 0.4s
                turnOnFlashlight();

                // Turn OFF after 0.4s
                flashHandler.postDelayed(() -> {
                    if (isFlashlightEnabled && currentFlickerMode == 2) {
                        turnOffFlashlight();
                    }
                }, 400);

                // Next burst after 0.67s (1.5 per second)
                flashHandler.postDelayed(this, 670);
            }
        };

        // Start first burst immediately
        flashHandler.post(flickerRunnable);
    }

    private void startFastFlicker() {
        // 200-400: Fast flicker - 2.5 flickers per second
        // Each burst: 0.2s ON, 0.2s OFF (total 0.4s per burst)
        flickerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isFlashlightEnabled || currentFlickerMode != 3) {
                    return;
                }

                // Turn ON for 0.2s
                turnOnFlashlight();

                // Turn OFF after 0.2s
                flashHandler.postDelayed(() -> {
                    if (isFlashlightEnabled && currentFlickerMode == 3) {
                        turnOffFlashlight();
                    }
                }, 200);

                // Next burst after 0.4s (2.5 per second)
                flashHandler.postDelayed(this, 400);
            }
        };

        // Start first burst immediately
        flashHandler.post(flickerRunnable);
    }

    private void startExtremeFlicker() {
        // 400+: Extreme flicker - 4 flickers per second (rapid strobe)
        // Each burst: 0.125s ON, 0.125s OFF (total 0.25s per burst)
        flickerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isFlashlightEnabled || currentFlickerMode != 4) {
                    return;
                }

                // Turn ON for 0.125s
                turnOnFlashlight();

                // Turn OFF after 0.125s
                flashHandler.postDelayed(() -> {
                    if (isFlashlightEnabled && currentFlickerMode == 4) {
                        turnOffFlashlight();
                    }
                }, 125);

                // Next burst after 0.25s (4 per second)
                flashHandler.postDelayed(this, 250);
            }
        };

        // Start first burst immediately
        flashHandler.post(flickerRunnable);
    }

    private void stopFlashlightFlicker() {
        if (flickerRunnable != null) {
            flashHandler.removeCallbacks(flickerRunnable);
            flickerRunnable = null;
        }
    }

    private void turnOnFlashlight() {
        if (isFlashlightOn) {
            android.util.Log.d("EMFGhost", "Flashlight already ON, skipping");
            return; // Already on, don't call API again
        }

        android.util.Log.d("EMFGhost", "Attempting to turn flashlight ON");
        try {
            if (cameraManager != null && cameraId != null) {
                cameraManager.setTorchMode(cameraId, true);
                isFlashlightOn = true;
                notifyFlashlightStatus();
                android.util.Log.d("EMFGhost", "Flashlight turned ON successfully");
            } else {
                android.util.Log.e("EMFGhost", "Cannot turn ON: cameraManager=" + cameraManager + ", cameraId=" + cameraId);
            }
        } catch (CameraAccessException e) {
            android.util.Log.e("EMFGhost", "Error turning flashlight ON", e);
        }
    }

    private void turnOffFlashlight() {
        if (!isFlashlightOn) {
            android.util.Log.d("EMFGhost", "Flashlight already OFF, skipping");
            return; // Already off, don't call API again
        }

        android.util.Log.d("EMFGhost", "Attempting to turn flashlight OFF");
        try {
            if (cameraManager != null && cameraId != null) {
                cameraManager.setTorchMode(cameraId, false);
                isFlashlightOn = false;
                notifyFlashlightStatus();
                android.util.Log.d("EMFGhost", "Flashlight turned OFF successfully");
            } else {
                android.util.Log.e("EMFGhost", "Cannot turn OFF: cameraManager=" + cameraManager + ", cameraId=" + cameraId);
            }
        } catch (CameraAccessException e) {
            android.util.Log.e("EMFGhost", "Error turning flashlight OFF", e);
        }
    }

    private void notifyFlashlightStatus() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof SensorDataListener) {
                ((SensorDataListener) fragment).onFlashlightStatusChanged(isFlashlightOn, currentFlickerFrequency);
            }
        }
    }

    public boolean isFlashlightOn() {
        return isFlashlightOn;
    }
}



