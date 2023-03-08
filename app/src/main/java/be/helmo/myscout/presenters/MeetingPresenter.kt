package be.helmo.myscout.presenters

import android.util.Log
import androidx.lifecycle.LifecycleService
import be.helmo.myscout.database.repository.MyScoutRepository
import be.helmo.myscout.factory.interfaces.IMeetingRecyclerCallback
import be.helmo.myscout.factory.interfaces.ISelectMeetingCallback
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.presenters.interfaces.IMeetingRowView
import be.helmo.myscout.presenters.viewmodel.MeetingListViewModel
import be.helmo.myscout.presenters.viewmodel.MeetingViewModel
import be.helmo.myscout.view.interfaces.IMeetingRecyclerCallbackPresenter
import be.helmo.myscout.view.interfaces.IMeetingsSelectMeetingCallback
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class MeetingPresenter(var myScoutRepository: MyScoutRepository
) : IMeetingRecyclerCallbackPresenter, IMeetingsSelectMeetingCallback, LifecycleService() {
    var meetingList: ArrayList<Meeting> = ArrayList<Meeting>() //liste meetings
    var meetingListViewModels: ArrayList<MeetingListViewModel> = ArrayList<MeetingListViewModel>() //list meetings ViewModels

    var recyclerCallback: IMeetingRecyclerCallback? = null
    var selectsMeetingCallback: ISelectMeetingCallback? = null

    init {
            GlobalScope.launch {
                myScoutRepository.meetings?.take(1)?.collect { meetings ->
                    for (meeting in meetings!!) {
                        val date = SimpleDateFormat("dd/MM/yyyy").format(meeting!!.startDate)
                        meetingList.add(meeting)
                        meetingListViewModels.add(
                            MeetingListViewModel(
                                date,
                                getAddressString(meeting.startLocation)
                            )
                        )
                        recyclerCallback?.onMeetingDataAdd(meetingListViewModels.size)
                    }
                }

            }

    }

    override fun onBindMeetingRowViewAtPosition(position: Int, rowView: IMeetingRowView) {
        val meeting = meetingListViewModels[position]
        rowView.setTitle(String.format("Réunion du %s", meeting.date))
        rowView.setAddress(meeting.address)
    }

    override fun getMeetingRowsCount() : Int {
        return meetingListViewModels.size
    }

    override fun addMeeting(startDateHour: String,
                   endDateHour: String,
                   startLocation: LatLng,
                   endLocation: LatLng,
                   description: String,
                   story: String) {
        val meet = Meeting(UUID.randomUUID(), description, story, Date(startDateHour), Date(endDateHour), startLocation, endLocation) //todo changer conversion dates
        myScoutRepository.insertMeeting(meet)
        meetingList.add(meet)
        //add to recylclerview
        GlobalScope.launch {
            meetingListViewModels.add(MeetingListViewModel(startDateHour, getAddressString(startLocation)))
            recyclerCallback?.onMeetingDataAdd(meetingListViewModels.size)
        }
    }

    override fun modifyMeeting(meetId: UUID,
                               startDateHour: String,
                               endDateHour: String,
                               startLocation: LatLng,
                               endLocation: LatLng,
                               description: String,
                               story: String) {

        meetingList.forEachIndexed { index, meeting ->
            if(meeting.id == meetId) {
                meeting.description = description
                meeting.story = story
                meeting.startDate = Date(startDateHour)
                meeting.endDate = Date(endDateHour)
                meeting.startLocation = startLocation
                meeting.endLocation = endLocation

                myScoutRepository.updateMeeting(meeting)

                GlobalScope.launch {
                    meetingListViewModels[index].date = startDateHour.slice(IntRange(0, 9))
                    meetingListViewModels[index].address = getAddressString(startLocation)
                }
            }
        }
    }

    override fun removeMeeting(swipedItemPosition: Int) {
        myScoutRepository.deleteMeeting(meetingList[swipedItemPosition])
        meetingList.removeAt(swipedItemPosition)
        meetingListViewModels.removeAt(swipedItemPosition)
    }

    override fun goToMeeting(position: Int) {
        val meeting = meetingList[position]
        val startdate = SimpleDateFormat("dd/MM/yyyy hh:mm").format(meeting.startDate)
        val enddate = SimpleDateFormat("dd/MM/yyyy hh:mm").format(meeting.endDate)

        GlobalScope.launch {
            selectsMeetingCallback?.onSelectedMeeting(
                MeetingViewModel(
                    meeting.id,
                    startdate,
                    enddate,
                    getAddressString(meeting.startLocation),
                    getAddressString(meeting.endLocation),
                    meeting.startLocation,
                    meeting.endLocation,
                    meeting.description,
                    meeting.story
                )
            )
        }
    }

    override fun setMeetingListCallback(iMeetingListCallback: IMeetingRecyclerCallback?) {
        recyclerCallback = iMeetingListCallback
    }

    override fun setSelectMeetingCallback(iSelectMeetingCallback: ISelectMeetingCallback?) {
        selectsMeetingCallback = iSelectMeetingCallback
    }

    fun getAddressString(location: LatLng): String {
        var address = "erreur"


        //clé api différente acceptant uniquement geocoding
        val url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s", location.latitude, location.longitude, "AIzaSyC1u56ll03xLq-7EqyRjDULFy0u-KVdcOA")
        val response = URL(url).readText()
        val jsonObject = JSONObject(response)
        val results = jsonObject.getJSONArray("results")
        if(results.length() > 0) {
            address = results.getJSONObject(0).getString("formatted_address")
        }

            Log.d("loc address", address)

        return address
    }

}
