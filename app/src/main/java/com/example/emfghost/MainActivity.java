package com.example.emfghost;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

        // Get sensor names
        if (magneticSensor != null) {
            magneticSensorName = magneticSensor.getName();
        }

        if (temperatureSensor != null) {
            temperatureSensorName = temperatureSensor.getName();
            isTemperatureSimulated = false;
        } else {
            temperatureSensorName = "Simulated (No hardware sensor)";
            isTemperatureSimulated = true;
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
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

            notifyFragments();
            updateSound();

        } else if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            currentTemperature = event.values[0];
            notifyFragments();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed
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
            // 60 μT = lowest pitch (0.5x), 500+ μT = highest pitch (2.0x)
            float pitch = 0.5f + ((currentMagneticField - 60) / 440f) * 1.5f;
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
}



