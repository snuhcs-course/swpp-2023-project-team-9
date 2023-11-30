package com.littlestudio.ui.drawing.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {
    private Paint mPaint = new Paint();
    private float radius = 32f;

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = canvas.getWidth();
        float height = canvas.getHeight();
        float cX = width / 2;
        float cY = height / 2;

        canvas.drawCircle(cX, cY, radius / 2, mPaint);
    }

    public void setCircleRadius(float r) {
        radius = r;
        invalidate();
    }

    public void setAlpha(int newAlpha) {
        int alpha = (newAlpha * 255) / 100;
        mPaint.setAlpha(alpha);
        invalidate();
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }
}

