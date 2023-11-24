package com.littlestudio.ui.drawing.widget;

import android.graphics.Path;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MyPath extends Path implements Serializable {
    public List<Action> actions = new ArrayList<>();
    private String fillType = String.valueOf(FillType.WINDING); // new field
    private boolean convex; // new field
    private boolean empty; // new field
    private boolean inverseFillType;

    public MyPath() {
        setFillType(FillType.WINDING);
    }
    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        inputStream.defaultReadObject();

        List<Action> copiedActions = new ArrayList<>(actions);
        for (Action action : copiedActions) {
            action.perform(this);
        }
    }

    @Override
    public void reset() {
        actions.clear();
        super.reset();
    }

    @Override
    public void moveTo(float x, float y) {
        actions.add(new Move(x, y));
        super.moveTo(x, y);
    }

    @Override
    public void lineTo(float x, float y) {
        actions.add(new Line(x, y));
        super.lineTo(x, y);
    }

    @Override
    public void quadTo(float x1, float y1, float x2, float y2) {
        actions.add(new Quad(x1, y1, x2, y2));
        super.quadTo(x1, y1, x2, y2);
    }

    @Override
    public boolean isEmpty() {
        return actions.isEmpty();
    }

    @Override
    @Deprecated
    public boolean isConvex() {
        // Your logic to determine if the path is convex
        return true; // or false based on your logic
    }
}

