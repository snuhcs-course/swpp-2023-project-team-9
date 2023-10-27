package com.littlestudio.ui.drawing.widget;

import android.graphics.Paint;

public class Stroke {
    public PaintOptions paint;
    public MyPath path;


    public Stroke() {
    }

    public Stroke(MyPath path, PaintOptions paint) {
        this.paint = paint;
        this.path = path;
    }
}