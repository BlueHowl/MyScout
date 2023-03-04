package be.helmo.myscout.presenters

import android.util.Log
import androidx.lifecycle.LifecycleService
import be.helmo.myscout.database.repository.MyScoutRepository
import be.helmo.myscout.factory.interfaces.IMeetingPresenterCallback
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.presenters.viewmodel.MeetingViewModel
import be.helmo.myscout.view.interfaces.IMeetingPresenter
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class MeetingPresenter(myScoutRepository: MyScoutRepository, callback: IMeetingPresenterCallback?) : IMeetingPresenter, LifecycleService() {

    var meetingViewModels: ArrayList<MeetingViewModel> = ArrayList<MeetingViewModel>()

    var myScoutRepository: MyScoutRepository

    val callback: IMeetingPresenterCallback?

    init {
        this.myScoutRepository = myScoutRepository
        this.callback = callback


        /* fonctionne mais pas avec le getAddress
        lifecycleScope.launch {
            myScoutRepository.meetings?.collect { meetings ->
                Log.d("deeeeee", "test")
                if (meetings!!.isNotEmpty()) {
                    for (meeting in meetings) {
                        val date = SimpleDateFormat("dd/MM/yyyy").format(meeting!!.startDate)
                        meetingViewModels.add(
                            MeetingViewModel(
                                date,
                                "getAddressString(meeting.startLocation)"
                            )
                        )
                    }
                }
            }
        }
        */

        /*
        myScoutRepository.meetings?.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            ?.onEach {
                Log.d("deeeeee", "test")
                if (it!!.isNotEmpty()) {
                    for (meeting in it) {
                        val date = SimpleDateFormat("dd/MM/yyyy").format(meeting!!.startDate)
                        meetingViewModels.add(
                            MeetingViewModel(
                                date,
                                getAddressString(meeting.startLocation)
                            )
                        )
                    }
                }
            }!!.collect()//launchIn(lifecycleScope)
        */

        //todo faire attention (doit charger avant l'affichage)
        GlobalScope.launch {
            myScoutRepository.meetings?.collect { meetings ->
                for (meeting in meetings!!) {
                    val date = SimpleDateFormat("dd/MM/yyyy").format(meeting!!.startDate)
                    meetingViewModels.add(
                        MeetingViewModel(
                            date,
                            getAddressString(meeting.startLocation)
                        )
                    )
                }

                callback?.onMeetingDataReady(meetingViewModels)
            }

        }

    }

    override fun onBindMeetingRowViewAtPosition(position: Int, rowView: IMeetingRowView) {
        val meeting = meetingViewModels[position]
        rowView.setTitle(String.format("Réunion du %s", meeting.date))
        rowView.setAddress(meeting.address)
    }

    override fun getMeetingRowsCount() : Int {
        return meetingViewModels.size;
    }

    override fun addMeeting(startDateHour: String,
                   endDateHour: String,
                   startLocation: LatLng,
                   endLocation: LatLng,
                   description: String,
                   story: String) {
        val meet = Meeting(UUID.randomUUID(), description, story, Date(startDateHour), Date(endDateHour), startLocation, endLocation) //todo changer conversion dates
        myScoutRepository.insertMeeting(meet)
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
