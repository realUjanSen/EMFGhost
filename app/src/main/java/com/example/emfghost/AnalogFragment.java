package com.example.emfghost;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AnalogFragment extends Fragment implements SensorDataListener {

    private AnalogGaugeView analogGauge;
    private TextView analogValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analog, container, false);

        analogGauge = view.findViewById(R.id.analogGauge);
        analogValue = view.findViewById(R.id.analogValue);

        return view;
    }

    @Override
    public void onMagneticFieldChanged(float magneticField) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            if (analogGauge != null) {
                analogGauge.setValue(magneticField);
            }
            if (analogValue != null) {
                analogValue.setText(String.format("%.2f μT", magneticField));
            }
        });
    }

    @Override
    public void onTemperatureChanged(float temperature) {
        // Analog view doesn't display temperature
    }

    @Override
    public void onRecordingStateChanged(boolean isRecording) {
        // Analog view doesn't show recording state
    }
}

