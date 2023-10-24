package com.example.trial;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgressBar extends View {
    private int progress = 0;
    private int max = 100;
    private int progressColor;
    private int backgroundColor;
    private float progressWidth;
    private Paint paint;
    private RectF rectF;

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar);
        progress = typedArray.getInt(R.styleable.CircularProgressBar_progress, 0);
        max = 100;
        progressColor = typedArray.getColor(R.styleable.CircularProgressBar_progressColor, 0xFF0000);
        backgroundColor = typedArray.getColor(R.styleable.CircularProgressBar_backgroundColor, 0xE0E0E0);
        progressWidth = typedArray.getDimension(R.styleable.CircularProgressBar_progressWidth, 12f);
        typedArray.recycle();

        paint = new Paint();
        paint.setAntiAlias(true);
        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY);
        float angle = 360f * progress / max;

        // Draw background circle
        paint.setColor(backgroundColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(progressWidth);
        canvas.drawCircle(centerX, centerY, radius - progressWidth / 2, paint);

        // Draw progress arc
        paint.setColor(progressColor);
        rectF.set(centerX - radius + progressWidth / 2, centerY - radius + progressWidth / 2,
                centerX + radius - progressWidth / 2, centerY + radius - progressWidth / 2);
        canvas.drawArc(rectF, -90, angle, false, paint);
    }

    public void setProgress(int progress) {
        if (progress < 0)
            this.progress = 0;
        else if (progress > max)
            this.progress = max;
        else
            this.progress = progress;
        invalidate();
    }
}
