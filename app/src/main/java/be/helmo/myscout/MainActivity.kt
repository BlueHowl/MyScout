package be.helmo.myscout

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import be.helmo.myscout.factory.PresenterSingletonFactory
import be.helmo.myscout.factory.interfaces.ISelectMeetingCallback
import be.helmo.myscout.factory.interfaces.ISelectPhaseCallback
import be.helmo.myscout.presenters.viewmodel.MeetingViewModel
import be.helmo.myscout.presenters.viewmodel.PhaseViewModel
import be.helmo.myscout.view.interfaces.IMeetingPresenter
import be.helmo.myscout.view.interfaces.IPhasePresenter
import be.helmo.myscout.view.meeting.EditMeetingFragment
import be.helmo.myscout.view.meeting.meetinglist.MeetingListFragment
import be.helmo.myscout.view.phases.EditPhaseFragment
import be.helmo.myscout.view.phases.favoritephaselist.FavoritePhaseListFragment
import be.helmo.myscout.view.phases.phaselist.PhaseListFragment
import com.google.android.material.snackbar.Snackbar
import java.util.*


class MainActivity : AppCompatActivity(), ISelectMeetingCallback, ISelectPhaseCallback {

    var phaseListFragment: PhaseListFragment? = null //todo faire différement?

    lateinit var meetingPresenter: IMeetingPresenter
    lateinit var phasesPresenter: IPhasePresenter //todo changer interface ?
    lateinit var currentMeetingUUID: UUID
    lateinit var currentPhaseUUID: UUID
    lateinit var elementAdd: ImageView

    lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isCameraPermissionAllowed = false
    private var isWritePermissionAllowed = false
    private var isReadPermissionAllowed = false
    private var isLocationCoarsePermissionAllowed = false
    private var isInternetPermissionAllowed = false
    private var isLocationFinePermissionAllowed = false
    private var isReadMediaPermissionAllowed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        //this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            isCameraPermissionAllowed = permissions[Manifest.permission.CAMERA] ?: isCameraPermissionAllowed
            isLocationCoarsePermissionAllowed = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: isLocationCoarsePermissionAllowed
            isInternetPermissionAllowed = permissions[Manifest.permission.INTERNET] ?: isInternetPermissionAllowed
            isLocationFinePermissionAllowed = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: isLocationFinePermissionAllowed
            isReadMediaPermissionAllowed = permissions[if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                isWritePermissionAllowed = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionAllowed
                isReadPermissionAllowed = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionAllowed
            }] ?: isReadMediaPermissionAllowed
        }

        checkAndRequestPermissions()

        appContext = applicationContext


        elementAdd = findViewById(R.id.add_element)
        val elementEdit = findViewById<ImageView>(R.id.edit_element)
        val elementDelete = findViewById<ImageView>(R.id.delete_element)


        elementAdd.setOnClickListener(::onAddMeetingClick)
        elementEdit.setOnClickListener(::onEditElementClick) //forcément un Meeting
        elementDelete.setOnClickListener(::onDeleteElementClick)

        meetingPresenter = PresenterSingletonFactory.instance!!.getMeetingPresenter()
        PresenterSingletonFactory.instance!!.getSelectMeetingCallbackMeetingsPresenter().setSelectMeetingCallback(this)

        phasesPresenter = PresenterSingletonFactory.instance!!.getPhasePresenter()
        PresenterSingletonFactory.instance!!.getSelectPhaseCallbackPhasesPresenter().setSelectPhaseCallback(this)

        //affiche le premier fragment
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, MeetingListFragment.newInstance()).commit()
    }

    private fun checkAndRequestPermissions() {
        isCameraPermissionAllowed = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        isWritePermissionAllowed = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        isReadPermissionAllowed = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        isLocationCoarsePermissionAllowed = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        isLocationFinePermissionAllowed = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        isReadMediaPermissionAllowed = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        val listPermissionsNeeded = ArrayList<String>()

        if (!isReadPermissionAllowed) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!isWritePermissionAllowed) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!isCameraPermissionAllowed) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (!isLocationCoarsePermissionAllowed) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (!isInternetPermissionAllowed) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET)
        }
        if (!isLocationFinePermissionAllowed) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!isReadMediaPermissionAllowed) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            permissionLauncher.launch(listPermissionsNeeded.toTypedArray())
        }
    }

    override fun onSelectedMeeting(meeting: MeetingViewModel) {
        currentMeetingUUID = meeting.meetId
        phaseListFragment = PhaseListFragment.newInstance()
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        phaseListFragment!!.setMeetingValues(meeting)
        phasesPresenter.getPhases(meeting.meetId, Date(meeting.startDate)) //todo deprecated

        fragmentTransaction.replace(R.id.fragment_container, phaseListFragment!!)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onSelectedPhase(phase: PhaseViewModel, images: ArrayList<Uri>?) {
        currentPhaseUUID = phase.phaseId!!
        val fragment = EditPhaseFragment.newInstance()
        val fragmentManager: FragmentManager = supportFragmentManager

        fragment.setPhaseValues(phase, images)

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onPhaseFavoriteDelete() {
        Toast.makeText(this, "Vous ne pouvez pas supprimer une phase favorite", Toast.LENGTH_SHORT).show()
    }

    fun onAddMeetingClick(View: View) {
        //si on est dans meetingList alors on affiche fragment création de meeting
        if(supportFragmentManager.findFragmentById(R.id.fragment_container) is MeetingListFragment){
            val fragment = EditMeetingFragment.newInstance()
            val fragmentManager: FragmentManager = this.supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

            //fragment.setMeetingValues(null)

            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }else if(supportFragmentManager.findFragmentById(R.id.fragment_container) is PhaseListFragment){
            val popupMenu = PopupMenu(this, elementAdd)
            popupMenu.menu.add(Menu.NONE, 0, 0,"Ajouter une nouvelle phase")
            popupMenu.menu.add(Menu.NONE, 1, 1,"Ajouter une phase parmis les favoris")
            //val subMenu = popupMenu.menu.addSubMenu(Menu.NONE, 1, 1,"Ajouter une phase parmis les favoris")

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    0 -> {
                        val fragment = EditPhaseFragment.newInstance()
                        val fragmentManager: FragmentManager = supportFragmentManager
                        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

                        fragment.setPhaseValues(PhaseViewModel(UUID.randomUUID(), "", "", 0L, "",false), null)

                        fragmentTransaction.replace(R.id.fragment_container, fragment)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                        true
                    }
                    1 -> {
                        val fragment = FavoritePhaseListFragment.newInstance()
                        val fragmentManager: FragmentManager = supportFragmentManager
                        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

                        fragmentTransaction.replace(R.id.fragment_container, fragment)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()

                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    fun onEditElementClick(view: View) {
        val fragment = EditMeetingFragment.newInstance()
        val fragmentManager: FragmentManager = this.supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragment.setMeetingValues(phaseListFragment!!.meeting!!)

        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun onDeleteElementClick(view: View) {
        if(supportFragmentManager.findFragmentById(R.id.fragment_container) is EditMeetingFragment) {
            confirmSnack(view, 0)
        } else if(supportFragmentManager.findFragmentById(R.id.fragment_container) is EditPhaseFragment) {
            confirmSnack(view, 1)
        }
    }

    fun confirmSnack(view: View, type: Int) {
        val snackbar: Snackbar = Snackbar.make(view, if(type == 0) "Supprimer la réunion ?" else "Supprimer la phase ?", Snackbar.LENGTH_LONG)
            .setAction("Supprimer") {
                if(type == 0) {
                    phasesPresenter.removePhasesImages(currentMeetingUUID)
                    meetingPresenter.removeMeeting(currentMeetingUUID)
                    onBackPressed()
                } else {
                    if(phasesPresenter.isPhaseNotFavorite(currentPhaseUUID)) {
                        phasesPresenter.removePhase(currentPhaseUUID)
                    }else{
                        Toast.makeText(this, "Vous ne pouvez pas supprimer une phase favorite", Toast.LENGTH_SHORT).show()
                    }
                }
                onBackPressed()
            }

        snackbar.setActionTextColor(getColor(R.color.red))
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(getColor(R.color.green))

        snackbar.show()
    }

    /**
     * Permet de récupèrer le context
     */
    companion object {

        lateinit  var appContext: Context

    }
}