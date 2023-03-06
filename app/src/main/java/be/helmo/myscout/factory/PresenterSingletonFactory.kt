package be.helmo.myscout.factory

import be.helmo.myscout.database.repository.MyScoutRepository
import be.helmo.myscout.imageRepository.ImageRepository
import be.helmo.myscout.presenters.MeetingPresenter
import be.helmo.myscout.presenters.PhasePresenter
import be.helmo.myscout.repositories.IImageRepository
import be.helmo.myscout.view.interfaces.IMeetingRecyclerCallbackPresenter
import be.helmo.myscout.view.interfaces.IMeetingSelectPhaseCallback
import be.helmo.myscout.view.interfaces.IPhaseRecyclerCallbackPresenter

class PresenterSingletonFactory() {

    val myScoutRepository: MyScoutRepository = MyScoutRepository.instance!!
    val imageRepository: IImageRepository = ImageRepository()

    val meetingPresenter: MeetingPresenter = MeetingPresenter(myScoutRepository)
    val phasePresenter: PhasePresenter = PhasePresenter(myScoutRepository, imageRepository)

    fun getRecyclerCallbackMeetingPresenter(): IMeetingRecyclerCallbackPresenter {
        return meetingPresenter
    }

    fun getSelectPhaseCallbackMeetingPresenter(): IMeetingSelectPhaseCallback {
        return meetingPresenter
    }

    fun getMeetingPresenter(): IMeetingRecyclerCallbackPresenter {
        return meetingPresenter
    }

    fun getPhasePresenter(): IPhaseRecyclerCallbackPresenter {
        return phasePresenter
    }

    companion object {
        var instance: PresenterSingletonFactory? = null
            get() {
                if (field == null) field = PresenterSingletonFactory()
                return field
            }
    }
}