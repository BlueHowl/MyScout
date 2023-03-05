package be.helmo.myscout.presenters

import android.net.Uri
import be.helmo.myscout.MainActivity

class PhasePresenter {
    private var mainView: MainActivity? = null

    fun setView(mainView: MainActivity) {
        this.mainView = mainView
    }
    fun addPhase(during: String, resume: String, images: ArrayList<Uri?>?) {
        //mainView?.onAddPhaseRequested()
    }

    fun abort() {
        //mainView?.onAbortRequested()
    }

    fun validate() {

    }

}