package be.helmo.myscout.factory

import be.helmo.myscout.database.repository.MyScoutRepository
import be.helmo.myscout.imageRepository.ImageRepository
import be.helmo.myscout.presenters.MeetingPresenter
import be.helmo.myscout.presenters.PhasePresenter
import be.helmo.myscout.repositories.IImageRepository
import be.helmo.myscout.view.interfaces.*

class PresenterSingletonFactory() {

    val myScoutRepository: MyScoutRepository = MyScoutRepository.instance!!
    val imageRepository: IImageRepository = ImageRepository()

    val meetingPresenter: MeetingPresenter = MeetingPresenter(myScoutRepository)
    val phasePresenter: PhasePresenter = PhasePresenter(myScoutRepository, imageRepository)

    //meeting
    fun getRecyclerCallbackMeetingPresenter(): IMeetingRecyclerCallbackPresenter {
        return meetingPresenter
    }

    fun getSelectMeetingCallbackMeetingsPresenter(): IMeetingsSelectMeetingCallback {
        return meetingPresenter
    }

    fun getMeetingPresenter(): IMeetingPresenter {
        return meetingPresenter
    }

    //phases
    fun getSelectPhaseCallbackPhasesPresenter(): IPhasesSelectPhaseCallback {
        return phasePresenter
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