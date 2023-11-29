package com.littlestudio.ui.drawing.widget;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import android.graphics.Color;

public class PaintOptionsTest {
    private PaintOptions paintOptions;

    @Before
    public void setUp() {
        paintOptions = new PaintOptions();
    }

    @Test
    public void testDefaultPaintOptions() {
        assertEquals(Color.BLACK, paintOptions.getColor());
        assertEquals(32f, paintOptions.getStrokeWidth(), 0.0f);
        assertEquals(255, paintOptions.getAlpha());
        assertFalse(paintOptions.isEraserOn());
    }

    @Test
    public void testCustomPaintOptions() {
        int color = Color.RED;
        float strokeWidth = 10f;
        int alpha = 128;
        boolean isEraserOn = true;

        PaintOptions customPaintOptions = new PaintOptions(color, strokeWidth, alpha, isEraserOn);

        assertEquals(color, customPaintOptions.getColor());
        assertEquals(strokeWidth, customPaintOptions.getStrokeWidth(), 0.0f);
        assertEquals(alpha, customPaintOptions.getAlpha());
        assertTrue(customPaintOptions.isEraserOn());
    }

    @Test
    public void testSetColor() {
        int newColor = Color.BLUE;
        paintOptions.setColor(newColor);
        assertEquals(newColor, paintOptions.getColor());
    }

    @Test
    public void testSetStrokeWidth() {
        float newStrokeWidth = 12f;
        paintOptions.setStrokeWidth(newStrokeWidth);
        assertEquals(newStrokeWidth, paintOptions.getStrokeWidth(), 0.0f);
    }

    @Test
    public void testSetAlpha() {
        int newAlpha = 192;
        paintOptions.setAlpha(newAlpha);
        assertEquals(newAlpha, paintOptions.getAlpha());
    }

    @Test
    public void testSetEraserOn() {
        paintOptions.setEraserOn(true);
        assertTrue(paintOptions.isEraserOn());
    }
}