package be.helmo.myscout

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import be.helmo.myscout.factory.PresenterSingletonFactory
import be.helmo.myscout.factory.interfaces.ISelectMeetingCallback
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.view.meeting.EditMeetingFragment
import be.helmo.myscout.view.meeting.meetinglist.MeetingListFragment
import be.helmo.myscout.view.phases.EditPhaseFragment
import be.helmo.myscout.view.phases.phaselist.PhaseListFragment


class MainActivity : AppCompatActivity(), ISelectMeetingCallback {
    lateinit var menuTitle: TextView
    lateinit var meetingAdd: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        //this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appContext = applicationContext

        menuTitle = findViewById(R.id.menu_title)
        meetingAdd = findViewById(R.id.add_element)
        meetingAdd.setOnClickListener(::onAddElementClick)

        val meetingPresenter = PresenterSingletonFactory.instance!!.getSelectPhaseCallbackMeetingPresenter()
        meetingPresenter.setSelectMeetingCallback(this)

        //affiche le premier fragment
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, MeetingListFragment.newInstance()).commit()
    }

    override fun onSelectedMeeting(meeting: Meeting) {
        val fragment = PhaseListFragment.newInstance(meeting)
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    /* todo doit aller dans onAddElementClick : definir une variable qui change l'action du bouton d'ajout
    override fun onSelectedPhase(phaseId: UUID?) {
        val fragment = PhaseFragment.newInstance(MainActivity())
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }*/

    fun onAddElementClick(view: View) {
        Log.d("click", "Add element")

        if(supportFragmentManager.findFragmentById(R.id.fragment_container) is MeetingListFragment) {
            //si on est dans meetingList alors on affiche fragment création de meeting
            val fragment = EditMeetingFragment.newInstance(null, false)
            val fragmentManager: FragmentManager = this.supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        } else if(supportFragmentManager.findFragmentById(R.id.fragment_container) is PhaseListFragment) {
            //todo changer le parametre de newInstance
            val fragment = EditPhaseFragment.newInstance()
            val fragmentManager: FragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    //todo ajouter callback retour au fragment principal

    /**
     * Permet de récupèrer le context
     */
    companion object {

        lateinit  var appContext: Context

    }
}