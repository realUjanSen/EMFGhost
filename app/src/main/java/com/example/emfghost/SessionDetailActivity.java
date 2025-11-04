package com.example.emfghost;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SessionDetailActivity extends AppCompatActivity {

    private LineChart emfChart;
    private LineChart tempChart;
    private TextView sessionInfo;

    private AppDatabase database;
    private Executor executor = Executors.newSingleThreadExecutor();
    private String sessionId;
    private EMFSessionEntity session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);

        emfChart = findViewById(R.id.emfChart);
        tempChart = findViewById(R.id.tempChart);
        sessionInfo = findViewById(R.id.sessionInfo);

        sessionId = getIntent().getStringExtra("sessionId");
        database = AppDatabase.getDatabase(this);


        loadSessionData();
    }

    private void loadSessionData() {
        executor.execute(() -> {
            session = database.sessionDao().getSessionById(sessionId);

            runOnUiThread(() -> {
                if (session != null && session.readings != null && !session.readings.isEmpty()) {
                    displaySessionInfo();
                    setupCharts();
                } else {
                    Toast.makeText(SessionDetailActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void displaySessionInfo() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault());
        String startDate = dateFormat.format(new Date(session.startTime));

        long durationSeconds = (session.endTime - session.startTime) / 1000;
        long minutes = durationSeconds / 60;
        long seconds = durationSeconds % 60;

        String info = String.format(Locale.getDefault(),
                "Started: %s\nDuration: %dm %ds\nTotal Readings: %d",
                startDate, minutes, seconds, session.readings.size());

        sessionInfo.setText(info);
    }

    private void setupCharts() {
        List<EMFReading> readings = session.readings;
        if (readings == null || readings.isEmpty()) {
            return;
        }

        long startTime = readings.get(0).getTimestamp();

        // Prepare EMF data
        List<Entry> emfEntries = new ArrayList<>();
        for (int i = 0; i < readings.size(); i++) {
            EMFReading reading = readings.get(i);
            float timeOffset = (reading.getTimestamp() - startTime) / 1000f; // Convert to seconds
            emfEntries.add(new Entry(timeOffset, (float) reading.getMagneticField()));
        }

        // Prepare Temperature data
        List<Entry> tempEntries = new ArrayList<>();
        for (int i = 0; i < readings.size(); i++) {
            EMFReading reading = readings.get(i);
            float timeOffset = (reading.getTimestamp() - startTime) / 1000f;
            tempEntries.add(new Entry(timeOffset, (float) reading.getTemperature()));
        }

        // Setup EMF Chart
        setupLineChart(emfChart, emfEntries, "Magnetic Field (μT)", Color.GREEN);

        // Setup Temperature Chart
        setupLineChart(tempChart, tempEntries, "Temperature (°C)", Color.CYAN);
    }

    private void setupLineChart(LineChart chart, List<Entry> entries, String label, int color) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setDrawFilled(false);
        dataSet.setHighLightColor(Color.WHITE);
        dataSet.setHighlightLineWidth(1.5f);

        // Make the crosshair/highlight line DASHED
        dataSet.enableDashedHighlightLine(10f, 5f, 0f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Styling
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        chart.getLegend().setTextColor(Color.WHITE);

        // X Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.DKGRAY);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.1fs", value);
            }
        });

        // Y Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.DKGRAY);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // Enable drawing of marker view
        chart.setDrawMarkers(true);

        // Configure highlight (crosshair) - make it dashed
        chart.setHighlightPerTapEnabled(true);
        chart.setHighlightPerDragEnabled(true);

        // Create and set custom marker view that shows near finger
        CustomMarkerView markerView = new CustomMarkerView(this, R.layout.marker_view);
        markerView.setChartView(chart);
        chart.setMarker(markerView);


        chart.invalidate();
    }
}
