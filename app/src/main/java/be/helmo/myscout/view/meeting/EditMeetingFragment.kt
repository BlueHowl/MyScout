package be.helmo.myscout.view.meeting

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import be.helmo.myscout.R
import be.helmo.myscout.factory.PresenterSingletonFactory
import be.helmo.myscout.view.interfaces.IMeetingRecyclerCallbackPresenter

import com.adevinta.leku.*
import com.google.android.gms.maps.model.LatLng
import java.util.*

const val ARG_PARAM_PRESENTER = "presenter"
const val ARG_PARAM_MEETID = "id"
const val ARG_PARAM_EDITMODE = "mode"

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class EditMeetingFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    lateinit var meetingPresenter: IMeetingRecyclerCallbackPresenter

    var meetId: UUID? = null
    var editMode: Boolean = false

    var dateField: Int = 0
    var tempDate: String? = null
    var startDateHour: String? = null
    var endDateHour: String? = null

    lateinit var lekuActivityResultLauncher: ActivityResultLauncher<Intent>
    var locationField: Int = 0
    var startLocation: LatLng? = null
    var endLocation: LatLng? = null

    lateinit var btnStartDateHour: Button
    lateinit var btnEndDateHour: Button
    lateinit var btnStartLocation: Button
    lateinit var btnEndLocation: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        meetingPresenter = PresenterSingletonFactory.instance!!.getMeetingPresenter()

        arguments?.let {
            meetId = it.getSerializable(ARG_PARAM_MEETID) as UUID?
            editMode = it.getBoolean(ARG_PARAM_EDITMODE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_meeting, container, false)

        //datehours
        btnStartDateHour = view.findViewById(R.id.startDateHour)
        btnStartDateHour.setOnClickListener(::onStartDateHourClick)

        btnEndDateHour = view.findViewById(R.id.endDateHour)
        btnEndDateHour.setOnClickListener(::onEndDateHourClick)

        //locations
        btnStartLocation = view.findViewById(R.id.startLocation)
        btnStartLocation.setOnClickListener(::onStartLocationClick)

        btnEndLocation = view.findViewById(R.id.endLocation)
        btnEndLocation.setOnClickListener(::onEndLocationClick)

        //editTexts
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val etStory = view.findViewById<EditText>(R.id.etStory)

        //bottombtns
        val btnCancel = view.findViewById<Button>(R.id.cancel)
        btnCancel.setOnClickListener {
            activity?.onBackPressed() //todo changer?
        }

        val btnAdd = view.findViewById<Button>(R.id.add)
        btnAdd.setOnClickListener {
            if(startDateHour != null && endDateHour != null && startLocation != null &&
                endLocation != null) { //&& etDescription.text.isNotEmpty() && etStory.text.isNotEmpty()) {
                meetingPresenter.addMeeting(
                    startDateHour!!,
                    endDateHour!!,
                    startLocation!!,
                    endLocation!!,
                    etDescription.text.toString(),
                    etStory.text.toString()
                )
                Log.d("addMeeting", "appel addMeeting")

                activity?.onBackPressed() //todo changer?
            } else {
                Log.d("addMeeting", "champs non remplis")
                //todo toast
            }
        }

        lekuActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d("Location Picker result", "OK")
                    val data = result.data
                    val latitude = data?.getDoubleExtra(LATITUDE, 0.0)
                    Log.d("LATITUDE", latitude.toString())
                    val longitude = data?.getDoubleExtra(LONGITUDE, 0.0)
                    Log.d("LONGITUDE", longitude.toString())

                    val address = data?.getStringExtra(LOCATION_ADDRESS)
                    Log.d("ADDRESS****", address.toString())
                    val postalcode = data?.getStringExtra(ZIPCODE)
                    Log.d("POSTALCODE****", postalcode.toString())

                    val addressForBtn = String.format("%s %s", address.toString(), postalcode.toString())

                    val location = LatLng(latitude!!, longitude!!)

                    if(locationField == 0) {
                        startLocation = location
                        btnStartLocation.text = addressForBtn
                    } else if(locationField == 1) {
                        endLocation = location
                        btnEndLocation.text = addressForBtn
                    }

                } else {
                    Log.d("Location Picker result", "CANCELLED") //todo Toast
                }
            }


        return view
    }

    //datehours
    fun onStartDateHourClick(view: View?) {
        dateField = 0
        createDateHourDialog()
    }

    fun onEndDateHourClick(view: View?) {
        dateField = 1
        createDateHourDialog()
    }

    fun createDateHourDialog() {
        val calendar: Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val datePickerDialog = DatePickerDialog(
            requireView().context,
            DatePickerDialog.OnDateSetListener(::onDateSet),
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        tempDate = String.format("%02d/%02d/%d", dayOfMonth, month, year)

        val calendar: Calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            requireView().context, TimePickerDialog.OnTimeSetListener(::onTimeSet), hour, minute,
            DateFormat.is24HourFormat(requireView().context)
        )
        timePickerDialog.show()
    }
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        if(dateField == 0) {
            startDateHour = String.format("%s %02d:%02d", tempDate, hourOfDay, minute)
            btnStartDateHour.text = startDateHour
            Log.d("startDateHour", startDateHour!!)
        } else if (dateField == 1) {
            endDateHour = String.format("%s %02d:%02d", tempDate, hourOfDay, minute)
            btnEndDateHour.text = endDateHour
            Log.d("startDateHour", endDateHour!!)
        }
    }


    //location
    fun onStartLocationClick(view: View?) {
        locationField = 0
        createLocationDialog()
    }

    fun onEndLocationClick(view: View?) {
        locationField = 1
        createLocationDialog()
    }

    fun createLocationDialog() {
        val locationPickerIntent = LocationPickerActivity.Builder()
            .withDefaultLocaleSearchZone()
            .shouldReturnOkOnBackPressed()
            .withZipCodeHidden()
            .withVoiceSearchHidden()
            .build(requireContext())

        lekuActivityResultLauncher.launch(locationPickerIntent)
    }


    companion object {
        private const val TAG = "EditMeetingFragment"
        fun newInstance(arg1: UUID?, arg2: Boolean): EditMeetingFragment {
            val fragment = EditMeetingFragment()

            val args = Bundle()
            args.putSerializable(ARG_PARAM_MEETID, arg1)
            args.putBoolean(ARG_PARAM_EDITMODE, arg2)
            fragment.arguments = args

            return fragment
        }
    }

}