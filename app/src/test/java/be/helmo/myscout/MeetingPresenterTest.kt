import be.helmo.myscout.database.repository.MyScoutRepository
import be.helmo.myscout.factory.interfaces.IMeetingRecyclerCallback
import be.helmo.myscout.factory.interfaces.ISelectMeetingCallback
import be.helmo.myscout.imageRepository.ImageRepository
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.presenters.MeetingPresenter
import be.helmo.myscout.presenters.interfaces.IMeetingRowView
import be.helmo.myscout.presenters.viewmodel.MeetingViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import net.bytebuddy.matcher.ElementMatchers.any
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.text.SimpleDateFormat
import java.util.*

class MeetingPresenterTest {

    @Mock
    lateinit var myScoutRepository: MyScoutRepository

    @Mock
    lateinit var meetingRecyclerCallback: IMeetingRecyclerCallback

    @Mock
    lateinit var selectMeetingCallback: ISelectMeetingCallback

    @Mock
    lateinit var meetingRowView: IMeetingRowView

    lateinit var meetingPresenter: MeetingPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        meetingPresenter = MeetingPresenter(myScoutRepository)
        meetingPresenter.recyclerCallback = meetingRecyclerCallback
        meetingPresenter.selectsMeetingCallback = selectMeetingCallback
    }

}
