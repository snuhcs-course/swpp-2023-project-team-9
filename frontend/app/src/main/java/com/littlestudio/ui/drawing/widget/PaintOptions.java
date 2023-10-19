package com.littlestudio.ui.drawing.widget;

import android.graphics.Color;

public class PaintOptions {

    private int color;
    private float strokeWidth;
    private int alpha;
    private boolean isEraserOn;

    public PaintOptions() {
        this.color = Color.BLACK;
        this.strokeWidth = 8f;
        this.alpha = 255;
        this.isEraserOn = false;
    }

    public PaintOptions(int color, float strokeWidth, int alpha, boolean isEraserOn) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.alpha = alpha;
        this.isEraserOn = isEraserOn;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public boolean isEraserOn() {
        return isEraserOn;
    }

    public void setEraserOn(boolean eraserOn) {
        isEraserOn = eraserOn;
    }
}

