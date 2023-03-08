package be.helmo.myscout.view.interfaces

import be.helmo.myscout.presenters.interfaces.IMeetingRowView
import com.google.android.gms.maps.model.LatLng
import java.util.*

interface IMeetingPresenter : IMeetingRecyclerCallbackPresenter {

    fun onBindMeetingRowViewAtPosition(position: Int, rowView: IMeetingRowView)

    fun getMeetingRowsCount() : Int

    fun addMeeting(startDateHour: String,
                   endDateHour: String,
                   startLocation: LatLng,
                   endLocation: LatLng,
                   description: String,
                   story: String)

    fun modifyMeeting(meetId: UUID,
                      startDateHour: String,
                      endDateHour: String,
                      startLocation: LatLng,
                      endLocation: LatLng,
                      description: String,
                      story: String)

    fun removeMeeting(swipedItemPosition: Int?)

    fun goToMeeting(position: Int)

}