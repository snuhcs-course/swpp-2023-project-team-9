package com.littlestudio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class DrawAdapterTest {

    private Context context;
    private ArrayList<String> imageList;
    private DrawAdapter drawAdapter;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        imageList = new ArrayList<>();
        imageList.add("image1.jpg");
        imageList.add("image2.jpg");
        drawAdapter = new DrawAdapter(context, imageList);
    }

    @Test
    public void testConstructor() {
        assertNotNull(drawAdapter);
        assertEquals(2, drawAdapter.getItemCount());
    }

    @Test
    public void testGetItemCount() {
        assertEquals(2, drawAdapter.getItemCount());
    }
}