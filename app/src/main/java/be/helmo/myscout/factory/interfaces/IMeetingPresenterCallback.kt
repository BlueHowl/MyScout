package be.helmo.myscout.factory.interfaces

import be.helmo.myscout.presenters.viewmodel.MeetingViewModel

interface IMeetingPresenterCallback {
    fun onMeetingDataReady(meetingViewModels: List<MeetingViewModel>)
}