package be.helmo.myscout.view.interfaces

import be.helmo.myscout.factory.interfaces.IPhaseRecyclerCallback

interface IPhaseRecyclerCallbackPresenter {

    fun setPhaseListCallback(iPhaseRecyclerCallback: IPhaseRecyclerCallback?)
}