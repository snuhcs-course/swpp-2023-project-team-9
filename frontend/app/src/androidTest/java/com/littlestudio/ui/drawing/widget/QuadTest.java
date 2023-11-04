package com.littlestudio.ui.drawing.widget;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import android.graphics.Path;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;

public class QuadTest {
    private Quad quad;

    @Before
    public void setUp() {

        quad = new Quad(1.0F, 2.0F, 3.0F, 4.0F);
    }

    @Test
    public void pathPerformTest() {

        Path mockPath = Mockito.mock(Path.class);


        quad.perform(mockPath);


        Mockito.verify(mockPath).quadTo(1.0F, 2.0F, 3.0F, 4.0F);
    }

    @Test
    public void writerPerformTest() throws IOException {

        StringWriter stringWriter = new StringWriter();


        quad.perform(stringWriter);


        String writtenContent = stringWriter.toString();


        assertEquals("Q1.0,2.0 3.0,4.0", writtenContent);
    }
}