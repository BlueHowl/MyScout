package be.helmo.myscout.factory

import android.util.Log
import be.helmo.myscout.database.repository.MyScoutRepository
import be.helmo.myscout.presenters.MeetingPresenter
import be.helmo.myscout.view.interfaces.IMeetingListPresenter

class PresenterSingletonFactory() {

    val myScoutRepository: MyScoutRepository = MyScoutRepository.instance!!

    val meetingPresenter: MeetingPresenter = MeetingPresenter(myScoutRepository)

    fun getMeetingListPresenter(): IMeetingListPresenter {
        return meetingPresenter
    }

    fun getMeetingPresenter(): IMeetingListPresenter {
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