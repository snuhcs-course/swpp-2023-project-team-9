package com.littlestudio.ui.drawing;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.littlestudio.R;
import com.littlestudio.data.datasource.UserLocalDataSource;
import com.littlestudio.data.model.User;
import com.littlestudio.ui.ImageActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class ImageActivityUITest {

    @Rule
    public ActivityScenarioRule<ImageActivity> mActivityRule = new ActivityScenarioRule<>(ImageActivity.class);
    @Rule
    public IntentsTestRule<ImageActivity> intentsTestRule = new IntentsTestRule<>(ImageActivity.class);
    public UserLocalDataSource userLocalDataSource;

    @Before
    public void setup() {
        userLocalDataSource = UserLocalDataSource.getInstance(InstrumentationRegistry.getInstrumentation().getTargetContext());
        User testUser = new User(1, "test", "test", "test", "test", 1, new Date());
        userLocalDataSource.setUser(testUser);
    }

    @Test
    public void zombieReferenceTest() {
        onView(ViewMatchers.withId(R.id.wave))
                .check(matches(isDisplayed()))
                .check(matches(withText("Zombie")));
    }

    @Test
    public void dabReferenceTest() {
        onView(withId(R.id.dab))
                .check(matches(isDisplayed()))
                .check(matches(withText("Dab")));
    }

    @Test
    public void jumpingReferenceTest() {
        onView(withId(R.id.jump))
                .check(matches(isDisplayed()))
                .check(matches(withText("Jump")));
    }

}
