package com.example.emfghost;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class AnalogGaugeView extends View {
    private Paint gaugePaint;
    private Paint needlePaint;
    private Paint centerPaint;
    private Paint textPaint;
    private float currentValue = 0f;
    private static final float MAX_VALUE = 600f;
    private static final float START_ANGLE = 180f;
    private static final float SWEEP_ANGLE = 180f;

    public AnalogGaugeView(Context context) {
        super(context);
        init();
    }

    public AnalogGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnalogGaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        gaugePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gaugePaint.setStyle(Paint.Style.STROKE);
        gaugePaint.setStrokeWidth(40f);

        needlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        needlePaint.setColor(0xFFFFFFFF);
        needlePaint.setStyle(Paint.Style.FILL);
        needlePaint.setShadowLayer(10f, 0f, 0f, 0xFFFFFFFF);

        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setColor(0xFF444444);
        centerPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(0xFF00FF00);
        textPaint.setTextSize(24f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setValue(float value) {
        this.currentValue = Math.min(value, MAX_VALUE);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height - 50;
        int radius = Math.min(width, height * 2) / 2 - 100;

        // Draw gradient arc
        RectF oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // Create gradient from green to red
        LinearGradient gradient = new LinearGradient(
            centerX - radius, centerY,
            centerX + radius, centerY,
            new int[]{0xFF00FF00, 0xFF7FFF00, 0xFFFFFF00, 0xFFFF7F00, 0xFFFF0000},
            new float[]{0f, 0.25f, 0.5f, 0.75f, 1f},
            Shader.TileMode.CLAMP
        );
        gaugePaint.setShader(gradient);
        canvas.drawArc(oval, START_ANGLE, SWEEP_ANGLE, false, gaugePaint);

        // Draw scale markers
        Paint markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(0xFFFFFFFF);
        markerPaint.setStrokeWidth(3f);

        for (int i = 0; i <= 10; i++) {
            float angle = START_ANGLE + (SWEEP_ANGLE * i / 10f);
            double angleRad = Math.toRadians(angle);
            float startX = (float) (centerX + (radius - 50) * Math.cos(angleRad));
            float startY = (float) (centerY + (radius - 50) * Math.sin(angleRad));
            float endX = (float) (centerX + (radius - 30) * Math.cos(angleRad));
            float endY = (float) (centerY + (radius - 30) * Math.sin(angleRad));
            canvas.drawLine(startX, startY, endX, endY, markerPaint);

            // Draw value labels
            float labelX = (float) (centerX + (radius - 80) * Math.cos(angleRad));
            float labelY = (float) (centerY + (radius - 80) * Math.sin(angleRad) + 10);
            String label = String.valueOf((int) (MAX_VALUE * i / 10f));
            canvas.drawText(label, labelX, labelY, textPaint);
        }

        // Calculate needle angle based on current value
        float valueRatio = currentValue / MAX_VALUE;
        float needleAngle = START_ANGLE + (SWEEP_ANGLE * valueRatio);
        double needleRad = Math.toRadians(needleAngle);

        // Draw needle
        Path needlePath = new Path();
        needlePath.moveTo(centerX, centerY);
        float needleLength = radius - 60;
        float needleEndX = (float) (centerX + needleLength * Math.cos(needleRad));
        float needleEndY = (float) (centerY + needleLength * Math.sin(needleRad));

        // Create needle shape
        float needleWidth = 15f;
        double perpRad = needleRad + Math.PI / 2;
        float baseX1 = (float) (centerX + needleWidth * Math.cos(perpRad));
        float baseY1 = (float) (centerY + needleWidth * Math.sin(perpRad));
        float baseX2 = (float) (centerX - needleWidth * Math.cos(perpRad));
        float baseY2 = (float) (centerY - needleWidth * Math.sin(perpRad));

        needlePath.moveTo(baseX1, baseY1);
        needlePath.lineTo(needleEndX, needleEndY);
        needlePath.lineTo(baseX2, baseY2);
        needlePath.close();

        canvas.drawPath(needlePath, needlePaint);

        // Draw center circle
        canvas.drawCircle(centerX, centerY, 20f, centerPaint);
    }
}

