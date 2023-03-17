package be.helmo.myscout.view.interfaces

import be.helmo.myscout.factory.interfaces.IMeetingRecyclerCallback

interface IMeetingRecyclerCallbackPresenter {

    fun setMeetingListCallback(iMeetingListCallback: IMeetingRecyclerCallback?)

}