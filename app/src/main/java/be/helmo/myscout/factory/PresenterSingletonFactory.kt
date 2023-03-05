package be.helmo.myscout.factory

import be.helmo.myscout.database.repository.MyScoutRepository
import be.helmo.myscout.presenters.MeetingPresenter
import be.helmo.myscout.view.interfaces.IMeetingRecyclerCallbackPresenter
import be.helmo.myscout.view.interfaces.IMeetingSelectPhaseCallback

class PresenterSingletonFactory() {

    val myScoutRepository: MyScoutRepository = MyScoutRepository.instance!!

    val meetingPresenter: MeetingPresenter = MeetingPresenter(myScoutRepository)

    fun getRecyclerCallbackMeetingPresenter(): IMeetingRecyclerCallbackPresenter {
        return meetingPresenter
    }

    fun getSelectPhaseCallbackMeetingPresenter(): IMeetingSelectPhaseCallback {
        return meetingPresenter
    }

    fun getMeetingPresenter(): IMeetingRecyclerCallbackPresenter {
        return meetingPresenter
    }

    companion object {
        var instance: PresenterSingletonFactory? = null
            get() {
                if (field == null) field = PresenterSingletonFactory()
                return field
            }
    }
}