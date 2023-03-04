package be.helmo.myscout.factory

import be.helmo.myscout.database.repository.MyScoutRepository
import be.helmo.myscout.factory.interfaces.IMeetingPresenterCallback
import be.helmo.myscout.presenters.MeetingPresenter
import be.helmo.myscout.view.interfaces.IMeetingPresenter

class PresenterSingletonFactory() {

    val myScoutRepository: MyScoutRepository = MyScoutRepository.instance!!

    fun getMeetingPresenter(iMeetingListCallback: IMeetingPresenterCallback?): IMeetingPresenter {
        return MeetingPresenter(myScoutRepository, iMeetingListCallback)
    }

    companion object {
        var instance: PresenterSingletonFactory? = null
            get() {
                if (field == null) field = PresenterSingletonFactory()
                return field
            }
    }
}