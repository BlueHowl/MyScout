package be.helmo.myscout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import be.helmo.myscout.phaselist.PhaseListFragment
import be.helmo.myscout.phaseeditor.PhaseFragment
import be.helmo.myscout.presenters.PhasePresenter
import java.util.*


class MainActivity : AppCompatActivity(), PhaseListFragment.ISelectPhase{
    private val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainMenu()
    }

    override fun onSelectedPhase(phaseId: UUID?) {
        val fragment = PhaseFragment.newInstance(MainActivity())
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onAbortRequested() {
        setContentView(R.layout.activity_main)
    }

    fun mainMenu() {
        if (currentFragment == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, PhaseListFragment.newInstance()).commit()
        }
    }
}