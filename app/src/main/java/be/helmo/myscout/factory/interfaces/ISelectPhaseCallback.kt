package be.helmo.myscout.factory.interfaces

import android.net.Uri
import be.helmo.myscout.model.Phase
import be.helmo.myscout.presenters.viewmodel.PhaseViewModel

interface ISelectPhaseCallback {

    fun onSelectedPhase(phase: PhaseViewModel, images: ArrayList<Uri>?)

    fun onPhaseFavoriteDelete()
}