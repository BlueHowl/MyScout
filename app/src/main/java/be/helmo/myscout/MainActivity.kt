package be.helmo.myscout

import android.content.Context
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
import java.util.*
import be.helmo.myscout.view.meeting.EditMeetingFragment
import be.helmo.myscout.view.meeting.meetinglist.MeetingListFragment


class MainActivity : AppCompatActivity(), ISelectMeetingCallback {
    private val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
    override fun onCreate(savedInstanceState: Bundle?) {
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        //this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appContext = applicationContext

        val meetingAdd = findViewById<ImageView>(R.id.add_element)
        meetingAdd.setOnClickListener(::onAddElementClick)

        val meetingPresenter = PresenterSingletonFactory.instance!!.getSelectPhaseCallbackMeetingPresenter()
        meetingPresenter.setSelectMeetingCallback(this)

        mainMenu()
    }

    override fun onSelectedMeeting(meetingId: UUID?) {

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

        val fragment = EditMeetingFragment.newInstance(null, false)
        val fragmentManager: FragmentManager = this.supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack("")
        fragmentTransaction.commit()
    }

    fun mainMenu() { //todo (affichage une fois) et back sur les autres fragments pour revenir là, pas besoin de recréer
        if (currentFragment == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, MeetingListFragment.newInstance()).commit()
        }
    }

    /**
     * Permet de récupèrer le context
     */
    companion object {

        lateinit  var appContext: Context

    }
}