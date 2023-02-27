package be.helmo.myscout.phaselist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import be.helmo.myscout.database.repository.MyScoutRepository.Companion.instance
import be.helmo.myscout.model.Phase
import java.util.*

class PhaseListViewModel : ViewModel() {
    fun getPhases(meetingUUID: UUID?): LiveData<List<Phase?>?>? {
        Log.d("PhaseListViewModel", "loadPhase")
        return instance!!.getPhases(meetingUUID)
    }

    fun addPhase(phase: Phase?) {
        instance!!.insertPhase(phase)
    }
}