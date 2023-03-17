package be.helmo.myscout.view.interfaces

import be.helmo.myscout.factory.interfaces.IFavoritePhaseRecyclerCallback
import be.helmo.myscout.presenters.interfaces.IFavoritePhaseRowView

interface IFavoritePhaseRecyclerCallbackPresenter {

        fun setFavoritePhaseListCallback(iFavoritePhaseListCallback: IFavoritePhaseRecyclerCallback?)

        fun getFavoritePhases()

        fun onBindFavoritePhaseRowViewAtPosition(position: Int, rowView: IFavoritePhaseRowView)

        fun getFavoritePhaseRowsCount() : Int

        fun copyFavoritePhase(position: Int)
}