package be.helmo.myscout



import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.helmo.myscout.view.meeting.EditMeetingFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditMeetingFragmentTest {

    @Test
    fun enterMeetingDetails_Success() {
        // Launch the EditMeetingFragment
        val scenario = launchFragmentInContainer<EditMeetingFragment>()

        // Enter the meeting title
        onView(withId(R.id.et_meeting_title))
            .perform(typeText("Team Meeting"))

        // Enter the participants
        onView(withId(R.id.et_participants))
            .perform(typeText("john@example.com, jane@example.com"))

        // Select the location
        onView(withId(R.id.btn_location))
            .perform(click())

        // TODO: Add code to select a location from the map

        // Select the date
        onView(withId(R.id.btn_date))
            .perform(click())

        // Select a date in the date picker
        onView(withClassName(equalTo(DatePicker::class.java.name)))
            .perform(setDate(2023, 3, 10))
        onView(withText("OK"))
            .perform(click())

        // Select the time
        onView(withId(R.id.btn_time))
            .perform(click())

        // Select a time in the time picker
        onView(withClassName(equalTo(TimePicker::class.java.name)))
            .perform(setTime(10, 30))
        onView(withText("OK"))
            .perform(click())

        // Enter the meeting description
        onView(withId(R.id.et_meeting_description))
            .perform(scrollTo(), typeText("Discuss the project status"))

        // Click the save button
        onView(withId(R.id.btn_save))
            .perform(scrollTo(), click())

        // Check if the meeting details are displayed in the MeetingDetailFragment
        onView(withId(R.id.tv_meeting_title))
            .check(matches(withText("Team Meeting")))
        onView(withId(R.id.tv_meeting_participants))
            .check(matches(withText("john@example.com, jane@example.com")))
        onView(withId(R.id.tv_meeting_location))
        // TODO: Add code to verify the selected location
        onView(withId(R.id.tv_meeting_date))
            .check(matches(withText("10/03/2023")))
        onView(withId(R.id.tv_meeting_time))
            .check(matches(withText("10:30 AM")))
        onView(withId(R.id.tv_meeting_description))
            .check(matches(withText("Discuss the project status")))
    }
}



/*import android.widget.DatePicker
import android.widget.TimePicker
import androidx.databinding.adapters.RatingBarBindingAdapter.setRating
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.helmo.myscout.view.interfaces.IMeetingPresenter
import com.google.android.gms.maps.model.LatLng
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.regex.Pattern.matches*/

/*@RunWith(AndroidJUnit4::class)
class EditMeetingFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        val mockPresenter = mock(IMeetingPresenter::class.java)
        //PresenterSingletonFactory.instance = mockPresenter
    }

    @Test
    fun testAddMeeting() {
        onView(withId(R.id.fab)).perform(click())

        // Fill in meeting details
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
        onView(withId(R.id.search)).perform(typeText("Eiffel Tower"), closeSoftKeyboard())
        onView(withText("Eiffel Tower")).perform(click())
        onView(withId(android.R.id.button1)).perform(click())

        onView(withId(R.id.endLocation)).perform(click())
        onView(withId(R.id.search)).perform(typeText("Arc de Triomphe"), closeSoftKeyboard())
        onView(withText("Arc de Triomphe")).perform(click())
        onView(withId(android.R.id.button1)).perform(click())

        onView(withId(R.id.etDescription)).perform(typeText("Meeting description"), closeSoftKeyboard())
        onView(withId(R.id.etStory)).perform(typeText("Meeting story"), closeSoftKeyboard())
        onView(withId(R.id.ratingBar)).perform(setRating(3.5f))

        onView(withId(R.id.add)).perform(click())

        // Verify that addMeeting() was called on the presenter with the correct parameters
        verify(mockPresenter).addMeeting(
            eq("2023-03-06 15:30"),
            eq("2023-03-06 16:30"),
            eq(LatLng(48.8583701, 2.2944813)),
            eq(LatLng(48.8737798, 2.2950395)),
            eq("Meeting description"),
            eq("Meeting story"),
            eq(3.5f)
        )

        // Verify that the user is taken back to the previous screen
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(R.string.app_meetings_title)))))
    }



}*/
