package be.helmo.myscout.view.interfaces

import be.helmo.myscout.factory.interfaces.IPhaseRecyclerCallback

interface IPhaseRecyclerCallbackPresenter : IPhasePresenter {

        fun setPhaseListCallback(iPhaseListCallback: IPhaseRecyclerCallback?)
}