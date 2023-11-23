package com.littlestudio.ui.drawing.widget;

import android.graphics.Path;

import com.littlestudio.ui.constant.ActionType;

import java.io.IOException;
import java.io.Writer;

public class Line implements Action {

    public float x;
    public float y;


    public ActionType type = ActionType.LINE;

    public Line() {
    }
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

