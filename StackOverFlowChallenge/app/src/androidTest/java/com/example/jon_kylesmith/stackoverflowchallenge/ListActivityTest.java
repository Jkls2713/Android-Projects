package com.example.jon_kylesmith.stackoverflowchallenge;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;
import android.widget.ListView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static junit.framework.Assert.assertNotNull;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.action.ViewActions.click;
import static junit.framework.Assert.assertTrue;
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
        onView(withId(R.id.listView)).check(matches(isDisplayed()));
    }

    @Test
    public void search() {
        try {
            Thread.sleep(5000);
            ListView listView = rule.getActivity().findViewById(R.id.listView);
            assertNotNull("ListView should not be null", listView);
            int initialCount = listView.getAdapter().getCount();
            onView(withId(R.id.menuSearch)).check(matches(isDisplayed()));
            onView(withId(R.id.menuSearch)).perform(click());
            onView(isAssignableFrom(EditText.class)).perform(typeText("Darin"), pressImeActionButton());
            int newCount = listView.getAdapter().getCount();
            assertTrue("New Count should be subset of Initial Count", newCount < initialCount);
            onData(anything()).inAdapterView(withId(R.id.listView)).atPosition(0).perform(click());
        } catch (Exception e) {
            fail("Should not throw Exception");
        }
    }

}