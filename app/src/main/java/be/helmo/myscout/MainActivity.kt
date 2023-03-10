package be.helmo.myscout

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import be.helmo.myscout.factory.PresenterSingletonFactory
import be.helmo.myscout.factory.interfaces.ISelectMeetingCallback
import be.helmo.myscout.factory.interfaces.ISelectPhaseCallback
import be.helmo.myscout.model.Phase
import be.helmo.myscout.presenters.viewmodel.MeetingViewModel
import be.helmo.myscout.presenters.viewmodel.PhaseViewModel
import be.helmo.myscout.view.interfaces.IMeetingPresenter
import be.helmo.myscout.view.interfaces.IPhasePresenter
import be.helmo.myscout.view.meeting.EditMeetingFragment
import be.helmo.myscout.view.meeting.meetinglist.MeetingListFragment
import be.helmo.myscout.view.phases.EditPhaseFragment
import be.helmo.myscout.view.phases.phaselist.PhaseListFragment
import com.google.android.material.snackbar.Snackbar
import java.util.*


class MainActivity : AppCompatActivity(), ISelectMeetingCallback, ISelectPhaseCallback {

    var phaseListFragment: PhaseListFragment? = null //todo faire différement?

    lateinit var meetingPresenter: IMeetingPresenter
    lateinit var phasesPresenter: IPhasePresenter //todo changer interface ?
    lateinit var currentMeetingUUID: UUID
    lateinit var currentPhaseUUID: UUID

    override fun onCreate(savedInstanceState: Bundle?) {
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        //this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appContext = applicationContext


        val elementAdd = findViewById<ImageView>(R.id.add_element)
        val elementEdit = findViewById<ImageView>(R.id.edit_element)
        val elementDelete = findViewById<ImageView>(R.id.delete_element)
        elementAdd.setOnClickListener(::onAddElementClick)
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

    override fun onSelectedPhase(phase: PhaseViewModel, images: ArrayList<Uri>) {
        currentPhaseUUID = phase.phaseId!!
        val fragment = EditPhaseFragment.newInstance()
        val fragmentManager: FragmentManager = supportFragmentManager

        fragment.setPhaseValues(phase, images)

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun onAddElementClick(view: View) {
        if(supportFragmentManager.findFragmentById(R.id.fragment_container) is MeetingListFragment) {
            //si on est dans meetingList alors on affiche fragment création de meeting
            val fragment = EditMeetingFragment.newInstance()
            val fragmentManager: FragmentManager = this.supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

            fragment.setMeetingValues(null)

            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        } else if(supportFragmentManager.findFragmentById(R.id.fragment_container) is PhaseListFragment) {
            val fragment = EditPhaseFragment.newInstance()
            val fragmentManager: FragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

            fragment.setPhaseValues(PhaseViewModel(UUID.randomUUID(), "", "", 0L, "",false), null)

            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    fun onEditElementClick(view: View) {
        val fragment = EditMeetingFragment.newInstance()
        val fragmentManager: FragmentManager = this.supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragment.setMeetingValues(phaseListFragment?.meeting)

        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun onDeleteElementClick(view: View) {
        if(supportFragmentManager.findFragmentById(R.id.fragment_container) is EditMeetingFragment) {
            onSnack(view, 0)
        } else if(supportFragmentManager.findFragmentById(R.id.fragment_container) is EditPhaseFragment) {
            onSnack(view, 1)
        }
    }

    fun onSnack(view: View, type: Int) {
        var snackbar: Snackbar? = null
        val validate = "Supprimer"

        if(type == 0) {
            snackbar = Snackbar.make(view, "Supprimer la réunion ?", Snackbar.LENGTH_LONG)
                .setAction(validate) {
                    meetingPresenter.removeMeeting(currentMeetingUUID)
                    onRemoveItemConfirm()
                }
        } else if(type == 1) {
            snackbar = Snackbar.make(view, "Supprimer la phase ?", Snackbar.LENGTH_LONG)
                .setAction(validate) {
                    //todo removePhase
                    phasesPresenter.removePhase(currentPhaseUUID)
                    onRemoveItemConfirm()
                }
        }

        snackbar?.setActionTextColor(getColor(R.color.red))
        val snackbarView = snackbar?.view
        snackbarView?.setBackgroundColor(getColor(R.color.green))

        snackbar?.show()
    }

    fun onRemoveItemConfirm() {
        onBackPressed() //todo changer?
        onBackPressed() //todo changer?
    }

    /**
     * Permet de récupèrer le context
     */
    companion object {

        lateinit  var appContext: Context

    }
}