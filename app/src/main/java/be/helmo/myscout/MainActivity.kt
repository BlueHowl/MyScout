package be.helmo.myscout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.helmo.myscout.phaseeditor.PhaseFragment
import be.helmo.myscout.phaselist.PhaseListFragment
import java.util.*

class MainActivity : AppCompatActivity(), PhaseListFragment.ISelectPhase{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, PhaseListFragment.newInstance()).commit()
        }
    }

    override fun onSelectedPhase(phaseId: UUID?) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PhaseFragment.newInstance(phaseId))
            .addToBackStack(null).commit()
    }
}