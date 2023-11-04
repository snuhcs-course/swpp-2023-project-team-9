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

public class LineTest {
    private Line line;

    @Before
    public void setUp() {
        // Initialize a Line object with specific coordinates
        line = new Line(1.0F, 2.0F);
    }

    @Test
    public void pathPerformTest() {
        // Create a mock Path
        Path mockPath = Mockito.mock(Path.class);

        // Call perform method with the mock Path
        line.perform(mockPath);

        // Verify that the path.lineTo method was called with the expected coordinates
        Mockito.verify(mockPath).lineTo(1.0F, 2.0F);
    }

    @Test
    public void writerPerformTest() throws IOException {
        // Create a StringWriter to capture the output
        StringWriter stringWriter = new StringWriter();

        // Call perform method with the StringWriter
        line.perform(stringWriter);

        // Extract the content written to the StringWriter
        String writtenContent = stringWriter.toString();

        // Verify that the written content matches the expected format
        assertEquals("L1.0,2.0", writtenContent);
    }
}