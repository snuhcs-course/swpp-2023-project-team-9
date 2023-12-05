package com.littlestudio.ui.drawing;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.littlestudio.R;
import com.littlestudio.data.datasource.UserLocalDataSource;
import com.littlestudio.data.model.User;
import com.littlestudio.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class MainActivityUITest {
    @Rule
    public ActivityScenarioRule<MainActivity> mActivityRule = new ActivityScenarioRule<>(MainActivity.class);
    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);
    public UserLocalDataSource userLocalDataSource;

    @Before
    public void setup() {
        userLocalDataSource = UserLocalDataSource.getInstance(InstrumentationRegistry.getInstrumentation().getTargetContext());
        User testUser = new User(1, "test", "test", "test", "test", 1, new Date());
        userLocalDataSource.setUser(testUser);
    }

    @Test
    public void bottomNavigationViewTest() {
        onView(ViewMatchers.withId(R.id.draw))
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
        onView(withId(R.id.draw))
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
        onView(withId(R.id.draw))
                .perform(click());

        onView(ViewMatchers.withText("What would you like to do?"))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        onView(ViewMatchers.withText("Join a drawing"))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}