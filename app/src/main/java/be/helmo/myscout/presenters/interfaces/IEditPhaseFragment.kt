package be.helmo.myscout.presenters.interfaces

import android.net.Uri
import be.helmo.myscout.model.Phase

interface IEditPhaseFragment {

    fun setPhaseValues(phase: Phase, images: ArrayList<Uri>?)

}