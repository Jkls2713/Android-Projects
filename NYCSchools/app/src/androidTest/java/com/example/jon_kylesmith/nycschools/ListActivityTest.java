package com.example.jon_kylesmith.nycschools;

import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static junit.framework.Assert.assertNotNull;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.action.ViewActions.click;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.anything;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class ListActivityTest {

    @Rule
    public ActivityTestRule<ListActivity> rule = new ActivityTestRule<>(ListActivity.class);

    @Test
    public void onCreate() {
        ListActivity listActivity = rule.getActivity();
        assertNotNull("ListActivity should not be null", listActivity);
        onView(withId(R.id.schoolsListView)).check(matches(isDisplayed()));
    }

    @Test
    public void tapListItem() {
        try {
            Thread.sleep(1000);
            onData(anything()).inAdapterView(withId(R.id.schoolsListView)).atPosition(0).perform(click());
            Thread.sleep(1000);
        } catch (Exception e) {
            fail("Should not throw Exception");
        }
    }
}
