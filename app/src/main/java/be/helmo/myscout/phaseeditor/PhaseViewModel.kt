package be.helmo.myscout.phaseeditor

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import be.helmo.myscout.database.repository.MyScoutRepository
import be.helmo.myscout.model.Phase
import java.util.*

class PhaseViewModel : ViewModel() {
    fun loadPhase(phaseId: UUID?) : LiveData<Phase?>? {
        return MyScoutRepository.instance!!.getPhase(phaseId)
    }

    fun savePhase(phase: Phase?) {
        MyScoutRepository.instance!!.updatePhase(phase)
    }
}