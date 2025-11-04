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
            }

            // Update LEDs
            updateLEDs(magneticField);
        });
    }

    @Override
    public void onTemperatureChanged(float temperature) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            if (tempValue != null) {
                tempValue.setText(String.format("%.3f", temperature));
            }
        });
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

        // Thresholds: 60, 120, 180, 240, 300
        if (magneticField >= 60) {
            led1.setBackgroundResource(R.drawable.led_green_bright);
        } else {
            led1.setBackgroundResource(R.drawable.led_green_dim);
        }

        if (magneticField >= 120) {
            led2.setBackgroundResource(R.drawable.led_lime_bright);
        } else {
            led2.setBackgroundResource(R.drawable.led_lime_dim);
        }

        if (magneticField >= 180) {
            led3.setBackgroundResource(R.drawable.led_yellow_bright);
        } else {
            led3.setBackgroundResource(R.drawable.led_yellow_dim);
        }

        if (magneticField >= 240) {
            led4.setBackgroundResource(R.drawable.led_orange_bright);
        } else {
            led4.setBackgroundResource(R.drawable.led_orange_dim);
        }

        if (magneticField >= 300) {
            led5.setBackgroundResource(R.drawable.led_red_bright);
        } else {
            led5.setBackgroundResource(R.drawable.led_red_dim);
        }
    }
}

