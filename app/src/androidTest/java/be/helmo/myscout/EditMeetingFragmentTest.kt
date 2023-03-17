package be.helmo.myscout


import android.view.View
import android.widget.DatePicker
import android.widget.RatingBar
import android.widget.TimePicker
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class EditMeetingFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testAddModifyRemoveMeeting() {
        onView(withId(R.id.add_element)).perform(click())

        onView(withId(R.id.startDateHour)).check(matches(isDisplayed()))
        onView(withId(R.id.endDateHour)).check(matches(isDisplayed()))
        onView(withId(R.id.startLocation)).check(matches(isDisplayed()))
        onView(withId(R.id.endLocation)).check(matches(isDisplayed()))
        onView(withId(R.id.etDescription)).check(matches(isDisplayed()))
        onView(withId(R.id.etStory)).check(matches(isDisplayed()))

        // add
        onView(withId(R.id.startDateHour)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(PickerActions.setDate(2023, 3, 6))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).perform(PickerActions.setTime(15, 30))
        onView(withId(android.R.id.button1)).perform(click())

        onView(withId(R.id.endDateHour)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(PickerActions.setDate(2023, 3, 6))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).perform(PickerActions.setTime(16, 30))
        onView(withId(android.R.id.button1)).perform(click())

        onView(withId(R.id.startLocation)).perform(click())
        onView(withText("Use this location")).perform(click())

        onView(withId(R.id.endLocation)).perform(click())
        onView(withText("Use this location")).perform(click())

        onView(withId(R.id.etDescription)).perform(typeText("Meeting description"), closeSoftKeyboard())
        onView(withId(R.id.etStory)).perform(typeText("Meeting story"), closeSoftKeyboard())

        onView(withId(R.id.add)).perform(click())


        //modify
        onView(withId(R.id.list)).check(matches(isDisplayed()))
        onView(withId(R.id.list)).perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        onView(withId(R.id.edit_element)).perform(click())

        onView(withId(R.id.ratingBar)).perform(actionWithAssertions(setRating(4f)))

        onView(withId(R.id.cancel)).perform(click())
        onView(withId(android.R.id.content)).perform(pressBack());


        //swipe delete
        onView(withId(R.id.list)).check(matches(isDisplayed()))
        onView(withId(R.id.list)).perform(actionOnItemAtPosition<ViewHolder>(0, swipeLeft()))

    }

    fun setRating(rating: Float): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return CoreMatchers.allOf(ViewMatchers.isDisplayed(), ViewMatchers.isAssignableFrom(
                    RatingBar::class.java))
            }

            override fun getDescription(): String {
                return "Set the rating on a RatingBar"
            }

            override fun perform(uiController: UiController?, view: View?) {
                val ratingBar = view as RatingBar
                ratingBar.rating = rating
            }
        }
    }

}
