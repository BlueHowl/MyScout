package be.helmo.myscout.view.meeting

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import be.helmo.myscout.R
import be.helmo.myscout.factory.PresenterSingletonFactory
import be.helmo.myscout.presenters.interfaces.ISetMeetingInfos
import be.helmo.myscout.presenters.viewmodel.MeetingViewModel
import be.helmo.myscout.view.interfaces.IMeetingRecyclerCallbackPresenter

import com.adevinta.leku.*
import com.google.android.gms.maps.model.LatLng
import java.util.*


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class EditMeetingFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, ISetMeetingInfos {
    lateinit var meetingPresenter: IMeetingRecyclerCallbackPresenter

    var meeting: MeetingViewModel? = null
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
    lateinit var etDescription: EditText
    lateinit var etStory: EditText

    lateinit var btnAdd: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        meetingPresenter = PresenterSingletonFactory.instance!!.getMeetingPresenter()
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
        etDescription = view.findViewById(R.id.etDescription)
        etStory = view.findViewById(R.id.etStory)

        //bottombtns
        val btnCancel = view.findViewById<Button>(R.id.cancel)
        btnCancel.setOnClickListener {
            activity?.onBackPressed() //todo changer?
        }

        btnAdd = view.findViewById(R.id.add)
        btnAdd.setOnClickListener {
            if(startDateHour != null && endDateHour != null && startLocation != null &&
                endLocation != null) { //&& etDescription.text.isNotEmpty() && etStory.text.isNotEmpty()) {
                if(editMode) {
                    meetingPresenter.modifyMeeting(
                        meeting!!.meetId,
                        startDateHour!!,
                        endDateHour!!,
                        startLocation!!,
                        endLocation!!,
                        etDescription.text.toString(),
                        etStory.text.toString())

                    Toast.makeText(context, "Réunion modifiée", Toast.LENGTH_LONG).show()
                } else {
                    meetingPresenter.addMeeting(
                        startDateHour!!,
                        endDateHour!!,
                        startLocation!!,
                        endLocation!!,
                        etDescription.text.toString(),
                        etStory.text.toString()
                    )
                    Toast.makeText(context, "Réunion ajoutée", Toast.LENGTH_LONG).show()
                }

                activity?.onBackPressed() //todo changer?
            } else {
                Toast.makeText(context, "Champs non remplis", Toast.LENGTH_LONG).show()
            }
        }

        //call après l'assignation des objets de la vue
        if(editMode) {
            applyMeetingValues()
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
                    Log.d("ADDRESS", address.toString())
                    val postalcode = data?.getStringExtra(ZIPCODE)
                    Log.d("POSTALCODE", postalcode.toString())

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
                    Toast.makeText(context, "Erreur lors de la récupération de la localisation", Toast.LENGTH_LONG).show()
                }
            }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("EditMeetingFragment", "onViewCreated called")

        //change le titre du menu
        val menuTitle = requireActivity().findViewById<TextView>(R.id.menu_title)
        menuTitle.text = getString(R.string.app_meeting_edit_title)

        //rend invisible le btn add_element
        val addElement = requireActivity().findViewById<ImageView>(R.id.add_element)
        addElement.visibility = View.GONE

        //rend le btn edit_element invisible
        val editElement = requireActivity().findViewById<ImageView>(R.id.edit_element)
        editElement.visibility = View.INVISIBLE
    }

    override fun setMeetingValues(meeting: MeetingViewModel?) {
        this.meeting = meeting
        if(meeting != null) {
            editMode = true

            startDateHour = meeting.startDate
            endDateHour = meeting.endDate
            startLocation = meeting.startLocation
            endLocation = meeting.endLocation
        }
    }

    fun applyMeetingValues() {
        btnStartDateHour.text = meeting?.startDate
        btnEndDateHour.text = meeting?.endDate

        btnStartLocation.text = meeting?.startAddress
        btnEndLocation.text = meeting?.endAddress

        etDescription.setText(meeting?.description)
        etStory.setText(meeting?.story)

        //texte du bouton devient modifier car si appel de cette fonction
        //alors on est dans le cas de modif
        btnAdd.setText(R.string.btn_modify)
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
        const val TAG = "EditMeetingFragment"
        fun newInstance(): EditMeetingFragment {
            return EditMeetingFragment()
        }
    }

}