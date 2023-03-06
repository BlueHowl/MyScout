package be.helmo.myscout.factory.interfaces

import android.net.Uri
import be.helmo.myscout.model.Phase

interface ISelectPhaseCallback {

    fun onSelectedPhase(phase: Phase, images: ArrayList<Uri>)

}