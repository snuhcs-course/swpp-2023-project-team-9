package com.littlestudio.ui.drawing.widget;

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