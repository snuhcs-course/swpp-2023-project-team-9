package com.littlestudio.ui.drawing.widget;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;

import android.graphics.Path;

public class MoveTest {
    private Move move;

    @Before
    public void setUp() {

        move = new Move(3.0F, 4.0F);
    }

    @Test
    public void pathPerformTest() {

        Path mockPath = Mockito.mock(Path.class);


        move.perform(mockPath);


        Mockito.verify(mockPath).moveTo(3.0F, 4.0F);
    }

    @Test
    public void writerPerformTest() throws IOException {

        StringWriter stringWriter = new StringWriter();


        move.perform(stringWriter);


        String writtenContent = stringWriter.toString();

        assertEquals("M3.0,4.0", writtenContent);
    }
}