package com.littlestudio.ui.drawing.widget;

import android.graphics.Path;

import java.io.IOException;
import java.io.Writer;

public class Move implements Action {

    private final float x;
    private final float y;

    public Move(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void perform(Path path) {
        path.moveTo(x, y);
    }

    @Override
    public void perform(Writer writer) throws IOException {
        writer.write("M" + x + "," + y);
    }
}

