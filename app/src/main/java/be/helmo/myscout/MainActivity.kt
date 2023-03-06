package be.helmo.myscout

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import be.helmo.myscout.factory.PresenterSingletonFactory
import be.helmo.myscout.factory.interfaces.ISelectMeetingCallback
import be.helmo.myscout.factory.interfaces.ISelectPhaseCallback
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.model.Phase
import be.helmo.myscout.presenters.viewmodel.MeetingViewModel
import be.helmo.myscout.view.meeting.EditMeetingFragment
import be.helmo.myscout.view.meeting.meetinglist.MeetingListFragment
import be.helmo.myscout.view.phases.EditPhaseFragment
import be.helmo.myscout.view.phases.phaselist.PhaseListFragment
import java.util.*


class MainActivity : AppCompatActivity(), ISelectMeetingCallback, ISelectPhaseCallback {

    var phaseListFragment: PhaseListFragment? = null //todo faire différement?

    override fun onCreate(savedInstanceState: Bundle?) {
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        //this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appContext = applicationContext


        val meetingAdd = findViewById<ImageView>(R.id.add_element)
        val meetingEdit = findViewById<ImageView>(R.id.edit_element)
        meetingAdd.setOnClickListener(::onAddElementClick)
        meetingEdit.setOnClickListener(::onEditElementClick) //forcément un Meeting

        val meetingPresenter = PresenterSingletonFactory.instance!!.getSelectMeetingCallbackMeetingsPresenter()
        meetingPresenter.setSelectMeetingCallback(this)

        val phasesPresenter = PresenterSingletonFactory.instance!!.getSelectPhaseCallbackPhasesPresenter()
        phasesPresenter.setSelectPhaseCallback(this)

        //affiche le premier fragment
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, MeetingListFragment.newInstance()).commit()
    }

    override fun onSelectedMeeting(meeting: MeetingViewModel) {
        phaseListFragment = PhaseListFragment.newInstance()
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        phaseListFragment!!.setMeetingValues(meeting)

        fragmentTransaction.replace(R.id.fragment_container, phaseListFragment!!)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onSelectedPhase(phase: Phase, images: ArrayList<Uri>) {
        val fragment = EditPhaseFragment.newInstance()
        val fragmentManager: FragmentManager = supportFragmentManager
        fragment.setPhaseValues(phase, images)

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }

    fun onAddElementClick(view: View) {
        Log.d("click", "Add element")

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
            fragment.setPhaseValues(Phase(UUID.randomUUID(), "", "", 0L, "", false), null)

            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    fun onEditElementClick(view: View) {
        val fragment = EditMeetingFragment.newInstance()
        val fragmentManager: FragmentManager = this.supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        fragment.setMeetingValues(phaseListFragment?.meeting) //todo illégal ??
    }

    /**
     * Permet de récupèrer le context
     */
    companion object {

        lateinit  var appContext: Context

    }
}