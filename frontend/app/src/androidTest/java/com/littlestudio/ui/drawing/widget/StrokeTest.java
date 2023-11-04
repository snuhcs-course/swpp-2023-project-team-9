package com.littlestudio.ui.drawing.widget;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StrokeTest {
    private Stroke stroke;
    private MyPath path;
    private PaintOptions paint;

    @Before
    public void setUp() {
        path = new MyPath();
        paint = new PaintOptions();
        stroke = new Stroke(path, paint);
    }

    @Test
    public void testGetPath() {
        assertEquals(path, stroke.path);
    }

    @Test
    public void testGetPaint() {
        assertEquals(paint, stroke.paint);
    }
}