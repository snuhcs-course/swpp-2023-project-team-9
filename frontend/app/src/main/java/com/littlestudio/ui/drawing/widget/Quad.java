package com.littlestudio.ui.drawing.widget;

import android.graphics.Path;

import java.io.IOException;
import java.io.Writer;

public class Quad implements Action {

    private final float x1;
    private final float y1;
    private final float x2;
    private final float y2;

    public Quad(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void perform(Path path) {
        path.quadTo(x1, y1, x2, y2);
    }

    @Override
    public void perform(Writer writer) throws IOException {
        writer.write("Q" + x1 + "," + y1 + " " + x2 + "," + y2);
    }
}
