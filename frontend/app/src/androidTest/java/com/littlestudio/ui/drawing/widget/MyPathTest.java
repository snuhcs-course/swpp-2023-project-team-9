package com.littlestudio.ui.drawing.widget;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

public class MyPathTest {
    private MyPath myPath;

    @Before
    public void setUp() {
        myPath = new MyPath();
    }

    @Test
    public void testAddMoveAction() {
        myPath.moveTo(1.0F, 2.0F);
        List<Action> actions = myPath.actions;
        assertEquals(1, actions.size());
        assertTrue(actions.get(0) instanceof Move);
    }

    @Test
    public void testAddLineAction() {
        myPath.lineTo(3.0F, 4.0F);
        List<Action> actions = myPath.actions;
        assertEquals(1, actions.size());
        assertTrue(actions.get(0) instanceof Line);
    }

    @Test
    public void testAddQuadAction() {
        myPath.quadTo(1.0F, 2.0F, 3.0F, 4.0F);
        List<Action> actions = myPath.actions;
        assertEquals(1, actions.size());
        assertTrue(actions.get(0) instanceof Quad);
    }

    @Test
    public void testResetPath() {
        myPath.moveTo(1.0F, 2.0F);
        myPath.reset();
        List<Action> actions = myPath.actions;
        assertTrue(actions.isEmpty());
        assertTrue(myPath.isEmpty());
    }
}