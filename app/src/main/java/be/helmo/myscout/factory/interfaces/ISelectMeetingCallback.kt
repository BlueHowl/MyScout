package be.helmo.myscout.factory.interfaces

import be.helmo.myscout.model.Meeting

interface ISelectMeetingCallback {

    fun onSelectedMeeting(meeting: Meeting)

}