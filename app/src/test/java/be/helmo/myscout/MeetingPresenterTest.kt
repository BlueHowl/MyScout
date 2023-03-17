import be.helmo.myscout.database.repository.MyScoutRepository
import be.helmo.myscout.factory.interfaces.IMeetingRecyclerCallback
import be.helmo.myscout.factory.interfaces.ISelectMeetingCallback
import be.helmo.myscout.imageRepository.ImageRepository
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.presenters.MeetingPresenter
import be.helmo.myscout.presenters.interfaces.IMeetingRowView
import be.helmo.myscout.presenters.viewmodel.MeetingViewModel
import be.helmo.myscout.repositories.IMyScoutRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import net.bytebuddy.matcher.ElementMatchers.any
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.text.SimpleDateFormat
import java.util.*

class MeetingPresenterTest {

    @Mock
    lateinit var myScoutRepository: IMyScoutRepository

    @Mock
    lateinit var meetingRecyclerCallback: IMeetingRecyclerCallback

    @Mock
    lateinit var selectMeetingCallback: ISelectMeetingCallback

    @Mock
    lateinit var meetingRowView: IMeetingRowView

    lateinit var meetingPresenter: MeetingPresenter

    @Before
    fun setUp() {
        myScoutRepository = mock(IMyScoutRepository::class.java)
        meetingRecyclerCallback = mock(IMeetingRecyclerCallback::class.java)
        selectMeetingCallback = mock(ISelectMeetingCallback::class.java)
        meetingRowView = mock(IMeetingRowView::class.java)
        meetingPresenter = MeetingPresenter(myScoutRepository)
        meetingPresenter.recyclerCallback = meetingRecyclerCallback
        meetingPresenter.selectsMeetingCallback = selectMeetingCallback
    }

    @Test
    fun onAddMeeting() {
        val tempDate = String.format("%02d/%02d/%d", 30, 3, 2001)
        val startDate = String.format("%s %02d:%02d", tempDate, 14, 51)
        val endDate = String.format("%s %02d:%02d", tempDate, 16, 51)
        val startLatLng = LatLng(0.0, 0.0)
        val endLatLng = LatLng(23.0, 23.0)
        meetingPresenter.addMeeting(startDate, endDate, startLatLng, endLatLng,"test","test",5F)
        meetingPresenter.meetingList[0].let {
            assertEquals(it.startDate, Date(startDate))
            assertEquals(it.endDate, Date(endDate))
            assertEquals(it.startLocation, startLatLng)
            assertEquals(it.endLocation, endLatLng)
            assertEquals(it.description, "test")
            assertEquals(it.story, "test")
            assertEquals(it.rating, 5F)
        }
        GlobalScope.launch {
            verify(meetingRecyclerCallback).onMeetingDataAdd(0)
            meetingPresenter.removeMeeting(meetingPresenter.meetingList[0].id)
        }
    }

    @Test
    fun onBindMeetingRowViewAtPosition() {
        val tempDate = String.format("%02d/%02d/%d", 30, 3, 2001)
        val startDate = String.format("%s %02d:%02d", tempDate, 14, 51)
        val endDate = String.format("%s %02d:%02d", tempDate, 16, 51)
        val startLatLng = LatLng(0.0, 0.0)
        val endLatLng = LatLng(23.0, 23.0)
        meetingPresenter.addMeeting(startDate, endDate, startLatLng, endLatLng,"test","test",5F)
        GlobalScope.launch {
            meetingPresenter.onBindMeetingRowViewAtPosition(0, meetingRowView)
            verify(meetingRowView).setTitle(String.format("Réunion du %s", SimpleDateFormat("dd/MM/yyyy").format(Date(startDate))))
            verify(meetingRowView).setAddress("0.0, 0.0")
            meetingPresenter.removeMeeting(meetingPresenter.meetingList[0].id)
        }
    }

    @Test
    fun getMeetingCount() {
        val tempDate = String.format("%02d/%02d/%d", 30, 3, 2001)
        val startDate = String.format("%s %02d:%02d", tempDate, 14, 51)
        val endDate = String.format("%s %02d:%02d", tempDate, 16, 51)
        val startLatLng = LatLng(0.0, 0.0)
        val endLatLng = LatLng(23.0, 23.0)
        meetingPresenter.addMeeting(startDate, endDate, startLatLng, endLatLng,"test","test",5F)
        GlobalScope.launch {
            assertEquals(meetingPresenter.getMeetingRowsCount(), 1)
            meetingPresenter.removeMeeting(meetingPresenter.meetingList[0].id)
        }
    }

    @Test
    fun onMeetingSelected() {
        val tempDate = String.format("%02d/%02d/%d", 30, 3, 2001)
        val startDate = String.format("%s %02d:%02d", tempDate, 14, 51)
        val endDate = String.format("%s %02d:%02d", tempDate, 16, 51)
        val startLatLng = LatLng(0.0, 0.0)
        val endLatLng = LatLng(23.0, 23.0)
        meetingPresenter.addMeeting(startDate, endDate, startLatLng, endLatLng,"test","test",5F)
        meetingPresenter.goToMeeting(0)
        val meeting = meetingPresenter.meetingList[0]
        GlobalScope.launch {
            verify(selectMeetingCallback).onSelectedMeeting(MeetingViewModel(meeting.id, meeting.startDate.toString(), meeting.endDate.toString(),getAddressString(meeting.startLocation), getAddressString(meeting.startLocation), meeting.startLocation, meeting.endLocation, meeting.description, meeting.story, meeting.rating))
            meetingPresenter.removeMeeting(meetingPresenter.meetingList[0].id)
        }
    }

    @Test
    fun onMeetingDeleted() {
        val tempDate = String.format("%02d/%02d/%d", 30, 3, 2001)
        val startDate = String.format("%s %02d:%02d", tempDate, 14, 51)
        val endDate = String.format("%s %02d:%02d", tempDate, 16, 51)
        val startLatLng = LatLng(0.0, 0.0)
        val endLatLng = LatLng(23.0, 23.0)
        meetingPresenter.addMeeting(startDate, endDate, startLatLng, endLatLng,"test","test",5F)
        GlobalScope.launch {
            meetingPresenter.removeMeeting(meetingPresenter.meetingList[0].id)
        }
        assertEquals(meetingPresenter.getMeetingRowsCount(), 0)
    }

    @Test
    fun onMeetingUpdated() {
        val tempDate = String.format("%02d/%02d/%d", 30, 3, 2001)
        val startDate = String.format("%s %02d:%02d", tempDate, 14, 51)
        val endDate = String.format("%s %02d:%02d", tempDate, 16, 51)
        val startLatLng = LatLng(0.0, 0.0)
        val endLatLng = LatLng(23.0, 23.0)
        meetingPresenter.addMeeting(startDate, endDate, startLatLng, endLatLng,"test","test",5F)
        GlobalScope.launch {
            meetingPresenter.modifyMeeting(meetingPresenter.meetingList[0].id, startDate, endDate, startLatLng, endLatLng,"test","test",5F)
            verify(meetingRecyclerCallback).onMeetingDataAdd(0)
            meetingPresenter.removeMeeting(meetingPresenter.meetingList[0].id)
        }
    }

    @Test
    fun onRemoveMeetingAt() {
        val tempDate = String.format("%02d/%02d/%d", 30, 3, 2001)
        val startDate = String.format("%s %02d:%02d", tempDate, 14, 51)
        val endDate = String.format("%s %02d:%02d", tempDate, 16, 51)
        val startLatLng = LatLng(0.0, 0.0)
        val endLatLng = LatLng(23.0, 23.0)
        meetingPresenter.addMeeting(startDate, endDate, startLatLng, endLatLng,"test","test",5F)
        GlobalScope.launch {
            meetingPresenter.removeMeetingAt(0)
            verify(meetingRecyclerCallback).onMeetingDataAdd(0)
        }
    }

    fun getAddressString(latLng: LatLng): String {
        return String.format("%s, %s oui", latLng.latitude, latLng.longitude)
    }
}
