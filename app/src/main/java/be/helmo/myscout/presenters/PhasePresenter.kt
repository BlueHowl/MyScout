package be.helmo.myscout.presenters

import be.helmo.myscout.MainActivity

class PhasePresenter {
    private var mainView: MainActivity? = null

    fun setView(mainView: MainActivity) {
        this.mainView = mainView
    }

    fun abort() {
        //mainView?.onAbortRequested()
    }

    fun validate() {

    }

}