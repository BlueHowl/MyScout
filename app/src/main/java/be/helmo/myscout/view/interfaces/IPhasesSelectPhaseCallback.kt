package be.helmo.myscout.view.interfaces

import be.helmo.myscout.factory.interfaces.ISelectPhaseCallback

interface IPhasesSelectPhaseCallback : IPhasePresenter {

    fun setSelectPhaseCallback(iSelectPhaseCallback: ISelectPhaseCallback?)

}