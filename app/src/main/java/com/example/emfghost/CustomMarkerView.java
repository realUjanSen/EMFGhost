package com.example.emfghost;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.Locale;

public class CustomMarkerView extends MarkerView {

    private final TextView tvContent;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        // Inflate the custom marker layout
        LayoutInflater.from(context).inflate(layoutResource, this, true);

        tvContent = findViewById(R.id.markerContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (tvContent != null) {
            String content = String.format(Locale.getDefault(),
                    "Time: %.1fs\nValue: %.6f",
                    e.getX(), e.getY());
            tvContent.setText(content);
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        // Position marker above the selected point
        return new MPPointF(-(getWidth() / 2f), -getHeight() - 10);
    }
}

