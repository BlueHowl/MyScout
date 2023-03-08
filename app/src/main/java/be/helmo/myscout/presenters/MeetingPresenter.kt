package be.helmo.myscout.presenters

import android.util.Log
import be.helmo.myscout.database.repository.MyScoutRepository
import be.helmo.myscout.factory.interfaces.IMeetingRecyclerCallback
import be.helmo.myscout.factory.interfaces.ISelectMeetingCallback
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.presenters.interfaces.IMeetingRowView
import be.helmo.myscout.presenters.viewmodel.MeetingListViewModel
import be.helmo.myscout.presenters.viewmodel.MeetingViewModel
import be.helmo.myscout.view.interfaces.IMeetingPresenter
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
) : IMeetingPresenter, IMeetingsSelectMeetingCallback {
    var meetingList: ArrayList<Meeting> = ArrayList<Meeting>() //liste meetings
    var meetingListViewModels: ArrayList<MeetingListViewModel> = ArrayList<MeetingListViewModel>() //list meetings ViewModels

    var currentMeetingViewModel: MeetingViewModel? = null

    var recylcerCallback: IMeetingRecyclerCallback? = null
    var selectsMeetingCallback: ISelectMeetingCallback? = null

    var meetingListPosition: Int? = null

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
                        recylcerCallback?.onMeetingDataAdd(meetingListViewModels.size)
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
        val meet = Meeting(UUID.randomUUID(), description, story, Date(startDateHour), Date(endDateHour), startLocation, endLocation, null) //todo changer conversion dates
        myScoutRepository.insertMeeting(meet)
        meetingList.add(meet)
        //add to recylclerview
        GlobalScope.launch {
            meetingListViewModels.add(MeetingListViewModel(startDateHour, getAddressString(startLocation)))
            recylcerCallback?.onMeetingDataAdd(meetingListViewModels.size)
        }
    }

    override fun modifyMeeting(meetId: UUID,
                               startDateHour: String,
                               endDateHour: String,
                               startLocation: LatLng,
                               endLocation: LatLng,
                               description: String,
                               story: String,
                               rating: Float) {

        meetingList.forEachIndexed { index, meeting ->
            if(meeting.id == meetId) {
                meeting.description = description
                meeting.story = story
                meeting.startDate = Date(startDateHour)
                meeting.endDate = Date(endDateHour)
                meeting.startLocation = startLocation
                meeting.endLocation = endLocation
                meeting.rating = rating

                myScoutRepository.updateMeeting(meeting)

                GlobalScope.launch {
                    //met a jour le viewModel courant
                    currentMeetingViewModel?.startDate = startDateHour
                    currentMeetingViewModel?.endDate = endDateHour
                    currentMeetingViewModel?.startAddress = getAddressString(meeting.startLocation)
                    currentMeetingViewModel?.endAddress = getAddressString(meeting.endLocation)
                    currentMeetingViewModel?.startLocation = meeting.endLocation
                    currentMeetingViewModel?.endLocation = meeting.endLocation
                    currentMeetingViewModel?.description = meeting.description
                    currentMeetingViewModel?.story = meeting.story
                    currentMeetingViewModel?.rating = meeting.rating

                    //met à jour le viewModel dans la liste
                    meetingListViewModels[index].date = startDateHour.slice(IntRange(0, 9))
                    meetingListViewModels[index].address = getAddressString(startLocation)
                }
            }
        }
    }

    override fun removeMeeting(swipedItemPosition: Int?) {
        if(swipedItemPosition != null) {
            myScoutRepository.deleteMeeting(meetingList[swipedItemPosition])
            meetingList.removeAt(swipedItemPosition)
            meetingListViewModels.removeAt(swipedItemPosition)
        } else {
            myScoutRepository.deleteMeeting(meetingList[meetingListPosition!!])
            meetingList.removeAt(meetingListPosition!!)
            meetingListViewModels.removeAt(meetingListPosition!!)
        }
    }

    override fun goToMeeting(position: Int) {
        meetingListPosition = position
        val meeting = meetingList[position]
        val startdate = SimpleDateFormat("dd/MM/yyyy hh:mm").format(meeting.startDate)
        val enddate = SimpleDateFormat("dd/MM/yyyy hh:mm").format(meeting.endDate)

        GlobalScope.launch {
            currentMeetingViewModel = MeetingViewModel(
                meeting.id,
                startdate,
                enddate,
                getAddressString(meeting.startLocation),
                getAddressString(meeting.endLocation),
                meeting.startLocation,
                meeting.endLocation,
                meeting.description,
                meeting.story,
                meeting.rating
            )
            selectsMeetingCallback?.onSelectedMeeting(currentMeetingViewModel!!)
        }
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
