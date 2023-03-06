package be.helmo.myscout.factory.interfaces

import be.helmo.myscout.presenters.viewmodel.MeetingViewModel

interface ISelectMeetingCallback {

    fun onSelectedMeeting(meeting: MeetingViewModel)

}