package com.littlestudio.ui.drawing.widget;

import android.graphics.Path;

import java.io.IOException;
import java.io.Writer;

public class Move implements Action {

    public float x;
    public float y;

    public Move(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public ActionType type = ActionType.MOVE;

    public Move() {
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

