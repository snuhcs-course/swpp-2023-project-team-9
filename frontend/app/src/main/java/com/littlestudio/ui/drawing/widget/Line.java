package com.littlestudio.ui.drawing.widget;

import android.graphics.Path;

import java.io.IOException;
import java.io.Writer;

public class Line implements Action {

    private final float x;
    private final float y;

    public Line(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void perform(Path path) {
        path.lineTo(x, y);
    }

    @Override
    public void perform(Writer writer) throws IOException {
        writer.write("L" + x + "," + y);
    }
}

