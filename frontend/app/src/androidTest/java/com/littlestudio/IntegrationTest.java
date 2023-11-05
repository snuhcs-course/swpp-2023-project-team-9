package com.littlestudio;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.matcher.RootMatchers;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.littlestudio.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class IntegrationTest {
    @Rule
    public ActivityScenarioRule<MainActivity> mActivityRule = new ActivityScenarioRule<>(MainActivity.class);
    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void bottomNavigationViewTest() {
        onView(withId(R.id.bottomNavigationView))
                .check(matches(isDisplayed()));

        onView(withId(R.id.gallery))
                .check(matches(isDisplayed()));

        onView(withId(R.id.mypage))
                .check(matches(isDisplayed()));

        onView(withId(R.id.gallery))
                .perform(click());

        onView(withId(R.id.mypage))
                .perform(click());
    }

    @Test
    public void createDrawingTest() {
        onView(withId(R.id.fab_add_draw))
                .perform(click());

        onView(ViewMatchers.withText("What would you like to do?"))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        onView(ViewMatchers.withText("Create a drawing"))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void joinDrawingTest() {
        onView(withId(R.id.fab_add_draw))
                .perform(click());

        onView(ViewMatchers.withText("What would you like to do?"))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        onView(ViewMatchers.withText("Join a drawing"))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}