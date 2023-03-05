package be.helmo.myscout.presenters

import android.util.Log
import androidx.lifecycle.LifecycleService
import be.helmo.myscout.database.repository.MyScoutRepository
import be.helmo.myscout.factory.interfaces.IMeetingRecyclerCallback
import be.helmo.myscout.factory.interfaces.ISelectMeetingCallback
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.presenters.viewmodel.MeetingViewModel
import be.helmo.myscout.view.interfaces.IMeetingRecyclerCallbackPresenter
import be.helmo.myscout.view.interfaces.IMeetingSelectPhaseCallback
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class MeetingPresenter(var myScoutRepository: MyScoutRepository
) : IMeetingRecyclerCallbackPresenter, IMeetingSelectPhaseCallback, LifecycleService() {
    var meetingList: ArrayList<Meeting> = ArrayList<Meeting>()
    var meetingViewModels: ArrayList<MeetingViewModel> = ArrayList<MeetingViewModel>()

    var recylcerCallback: IMeetingRecyclerCallback? = null
    var selectsMeetingCallback: ISelectMeetingCallback? = null

    init {
            GlobalScope.launch {
                myScoutRepository.meetings?.take(1)?.collect { meetings ->
                    for (meeting in meetings!!) {
                        val date = SimpleDateFormat("dd/MM/yyyy").format(meeting!!.startDate)
                        meetingList.add(meeting)
                        meetingViewModels.add(
                            MeetingViewModel(
                                date,
                                getAddressString(meeting.startLocation)
                            )
                        )
                        recylcerCallback?.onMeetingDataAdd(meetingViewModels.size)
                    }
                }

            }

    }

    override fun onBindMeetingRowViewAtPosition(position: Int, rowView: IMeetingRowView) {
        val meeting = meetingViewModels[position]
        rowView.setTitle(String.format("Réunion du %s", meeting.date))
        rowView.setAddress(meeting.address)
    }

    override fun getMeetingRowsCount() : Int {
        return meetingViewModels.size
    }

    override fun addMeeting(startDateHour: String,
                   endDateHour: String,
                   startLocation: LatLng,
                   endLocation: LatLng,
                   description: String,
                   story: String) {
        val meet = Meeting(UUID.randomUUID(), description, story, Date(startDateHour), Date(endDateHour), startLocation, endLocation) //todo changer conversion dates
        myScoutRepository.insertMeeting(meet)

        //add to recylclerview
        GlobalScope.launch {
            meetingViewModels.add(MeetingViewModel(startDateHour, getAddressString(startLocation)))
            recylcerCallback?.onMeetingDataAdd(meetingViewModels.size)
        }
    }

    override fun removeMeeting(swipedItemPosition: Int) {
        myScoutRepository.deleteMeeting(meetingList[swipedItemPosition])
        meetingViewModels.removeAt(swipedItemPosition) //todo déjà remove ou pas ?
    }

    override fun goToMeeting(position: Int) {
        selectsMeetingCallback?.onSelectedMeeting(meetingList[position].id)
    }

    override fun setMeetingListCallback(iMeetingListCallback: IMeetingRecyclerCallback?) {
        recylcerCallback = iMeetingListCallback
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
