package be.helmo.myscout.view.interfaces

import be.helmo.myscout.factory.interfaces.IMeetingPresenterCallback

interface IMeetingListPresenter : IMeetingPresenter{

    fun setMeetingListCallback(iMeetingListCallback: IMeetingPresenterCallback?)

}