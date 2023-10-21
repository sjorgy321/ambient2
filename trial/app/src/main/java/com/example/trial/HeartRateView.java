package com.example.trial;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class HeartRateView extends View {
    private Path heartPath;
    private Paint paint;
    private int heartRate = 70; // Initial heart rate

    public HeartRateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        heartPath = new Path();
        paint = new Paint();
        paint.setColor(0xFFFF0000); // Red color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHeart(canvas);
        // You can add more dynamic elements like squiggly lines here
    }

    private void drawHeart(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int baseSize = Math.min(width, height) / 4;

        // Drawing a simple heart shape
        heartPath.moveTo(centerX, centerY - baseSize);
        heartPath.cubicTo(centerX + baseSize, centerY - baseSize, centerX, centerY + baseSize, centerX - baseSize, centerY - baseSize);
        canvas.drawPath(heartPath, paint);
    }

    public void setHeartRate(int rate) {
        this.heartRate = rate;
        // Update the UI when the heart rate changes
        invalidate();
    }
}
