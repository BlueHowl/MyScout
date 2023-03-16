package be.helmo.myscout.view.interfaces

import be.helmo.myscout.factory.interfaces.IPhaseRecyclerCallback
import be.helmo.myscout.presenters.interfaces.IPhaseRowView

interface IPhaseRecyclerCallbackPresenter {//: IPhasePresenter {

        fun setPhaseListCallback(iPhaseListCallback: IPhaseRecyclerCallback?)

        fun onBindPhaseRowViewAtPosition(position: Int, rowView: IPhaseRowView)

        fun getPhaseRowsCount() : Int

        fun goToPhase(position: Int)

        fun movePhase(position: Int, toPosition: Int)

        fun removePhaseAt(index: Int)
}