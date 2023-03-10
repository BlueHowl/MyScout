package be.helmo.myscout.presenters.interfaces

import android.net.Uri
import be.helmo.myscout.model.Phase
import be.helmo.myscout.presenters.viewmodel.PhaseViewModel

interface IEditPhaseFragment {

    fun setPhaseValues(phase: PhaseViewModel, images: ArrayList<Uri>?)

}