package com.littlestudio.ui.drawing;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.content.Intent;


import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.littlestudio.R;
import com.littlestudio.ui.drawing.DrawingActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DrawingActivityTest {

//    @Rule
//    public ActivityScenarioRule<DrawingActivity> activityRule = new ActivityScenarioRule<>(
//            new Intent(getApplicationContext(), DrawingActivity.class)
//    );
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