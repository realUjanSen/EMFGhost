package com.example.emfghost;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DigitalFragment extends Fragment implements SensorDataListener {

    private View led1, led2, led3, led4, led5;
    private TextView emfValue, tempValue, recordingStatus;
    private TextView magneticSensorInfo, temperatureSensorInfo;
    private Button recordButton, viewRecordsButton;
    private boolean isRecording = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_digital, container, false);

        // Initialize views
        led1 = view.findViewById(R.id.led1);
        led2 = view.findViewById(R.id.led2);
        led3 = view.findViewById(R.id.led3);
        led4 = view.findViewById(R.id.led4);
        led5 = view.findViewById(R.id.led5);
        emfValue = view.findViewById(R.id.emfValue);
        tempValue = view.findViewById(R.id.tempValue);
        recordingStatus = view.findViewById(R.id.recordingStatus);
        recordButton = view.findViewById(R.id.recordButton);
        viewRecordsButton = view.findViewById(R.id.viewRecordsButton);
        magneticSensorInfo = view.findViewById(R.id.magneticSensorInfo);
        temperatureSensorInfo = view.findViewById(R.id.temperatureSensorInfo);

        // Display sensor information
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            magneticSensorInfo.setText("Sensor: " + mainActivity.getMagneticSensorName());
            temperatureSensorInfo.setText("Sensor: " + mainActivity.getTemperatureSensorName());
        }

        // Set up button listeners
        recordButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).toggleRecording();
            }
        });

        viewRecordsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RecordsActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onMagneticFieldChanged(float magneticField) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            // Update EMF value
            if (emfValue != null) {
                emfValue.setText(String.format("%.6f", magneticField));

                // Change color based on magnetic field
                // 0-60: Green (#00FF00)
                // 600+: Red (#FF0000)
                // Gradient in between
                int color = getMagneticFieldColor(magneticField);
                emfValue.setTextColor(color);
            }

            // Update LEDs
            updateLEDs(magneticField);
        });
    }

    private int getMagneticFieldColor(float magneticField) {
        // Normalize to 0-1 range (0 to 600+)
        float normalized = Math.min(1f, magneticField / 600f);

        // Interpolate from green to red
        // Green: RGB(0, 255, 0) = #00FF00
        // Red: RGB(255, 0, 0) = #FF0000

        int r = (int)(0 * (1 - normalized) + 255 * normalized);
        int g = (int)(255 * (1 - normalized) + 0 * normalized);
        int b = 0;

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    @Override
    public void onTemperatureChanged(float temperature) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            if (tempValue != null) {
                tempValue.setText(String.format("%.3f", temperature));

                // Change color based on temperature
                // 25-28°C: Green (#00FF00)
                // 10°C and below: Violet (#9400D3)
                // Gradient in between
                int color = getTemperatureColor(temperature);
                tempValue.setTextColor(color);
            }
        });
    }

    private int getTemperatureColor(float temperature) {
        // Cap temperature range: 10°C (violet) to 28°C (green)
        float clamped = Math.max(10f, Math.min(28f, temperature));

        // Normalize to 0-1 range
        float normalized = (clamped - 10f) / (28f - 10f);

        // Interpolate from violet to green
        // Violet: RGB(148, 0, 211) = #9400D3
        // Green: RGB(0, 255, 0) = #00FF00

        int r = (int)(148 * (1 - normalized) + 0 * normalized);
        int g = (int)(0 * (1 - normalized) + 255 * normalized);
        int b = (int)(211 * (1 - normalized) + 0 * normalized);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    @Override
    public void onRecordingStateChanged(boolean recording) {
        if (getActivity() == null) return;
        isRecording = recording;

        getActivity().runOnUiThread(() -> {
            if (recordButton != null) {
                if (recording) {
                    recordButton.setText("STOP RECORDING");
                    recordButton.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_red_dark));
                } else {
                    recordButton.setText("START RECORDING");
                    recordButton.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_green_dark));
                }
            }

            if (recordingStatus != null) {
                recordingStatus.setVisibility(recording ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void updateLEDs(float magneticField) {
        if (led1 == null) return;

        // Thresholds adjusted for 0-600+ range: 60, 150, 250, 400, 550
        if (magneticField >= 60) {
            led1.setBackgroundResource(R.drawable.led_green_bright);
        } else {
            led1.setBackgroundResource(R.drawable.led_green_dim);
        }

        if (magneticField >= 150) {
            led2.setBackgroundResource(R.drawable.led_lime_bright);
        } else {
            led2.setBackgroundResource(R.drawable.led_lime_dim);
        }

        if (magneticField >= 250) {
            led3.setBackgroundResource(R.drawable.led_yellow_bright);
        } else {
            led3.setBackgroundResource(R.drawable.led_yellow_dim);
        }

        if (magneticField >= 400) {
            led4.setBackgroundResource(R.drawable.led_orange_bright);
        } else {
            led4.setBackgroundResource(R.drawable.led_orange_dim);
        }

        if (magneticField >= 550) {
            led5.setBackgroundResource(R.drawable.led_red_bright);
        } else {
            led5.setBackgroundResource(R.drawable.led_red_dim);
        }
    }
}

