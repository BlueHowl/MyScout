package be.helmo.myscout.view.interfaces

import be.helmo.myscout.presenters.interfaces.IMeetingRowView
import com.google.android.gms.maps.model.LatLng
import java.util.*

interface IMeetingPresenter :  IMeetingsSelectMeetingCallback, IMeetingRecyclerCallbackPresenter {

    fun onBindMeetingRowViewAtPosition(position: Int, rowView: IMeetingRowView)

    fun getMeetingRowsCount() : Int

    fun addMeeting(startDateHour: String,
                   endDateHour: String,
                   startLocation: LatLng,
                   endLocation: LatLng,
                   description: String,
                   story: String,
                   rating: Float)

    fun modifyMeeting(meetId: UUID,
                      startDateHour: String,
                      endDateHour: String,
                      startLocation: LatLng,
                      endLocation: LatLng,
                      description: String,
                      story: String,
                      rating: Float)

    fun removeMeeting(swipedItemPosition: UUID)

    fun removeMeetingAt(index: Int)

    fun goToMeeting(position: Int)

}