package be.helmo.myscout.view.interfaces

import be.helmo.myscout.presenters.IPhaseRowView

interface IPhasePresenter {

    fun onBindPhaseRowViewAtPosition(position: Int, rowView: IPhaseRowView)

    fun getPhaseRowsCount() : Int

    fun addPhase(during: String,
                 description: String,
                 images: String)

    fun removePhase(swipeItemPosition: Int)

    fun goToPhase(position: Int)
}