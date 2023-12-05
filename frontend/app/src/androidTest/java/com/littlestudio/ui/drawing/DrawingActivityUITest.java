package com.littlestudio.ui.drawing;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.littlestudio.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DrawingActivityUITest {

    @Rule
    public ActivityTestRule<DrawingActivity> activityRule = new ActivityTestRule<>(DrawingActivity.class);

    @Test
    public void drawingActivityTest_drawView_OK() {


        DrawingActivity activity = activityRule.getActivity();


        Espresso.onView(ViewMatchers.withId(R.id.draw_view))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

    @Test
    public void drawingActivityTest_imageSketchBook_OK() {

        DrawingActivity activity = activityRule.getActivity();

        Espresso.onView(ViewMatchers.withId(R.id.image_sketchbook))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

    @Test
    public void drawingActivityTest_imageClose_OK() {

        DrawingActivity activity = activityRule.getActivity();

        Espresso.onView(ViewMatchers.withId(R.id.image_close_drawing))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }
}