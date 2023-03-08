package be.helmo.myscout.presenters

import android.graphics.Bitmap
import android.net.Uri
import be.helmo.myscout.database.repository.MyScoutRepository
import be.helmo.myscout.factory.interfaces.IPhaseRecyclerCallback
import be.helmo.myscout.factory.interfaces.ISelectPhaseCallback
import be.helmo.myscout.model.Phase
import be.helmo.myscout.presenters.interfaces.IPhaseRowView
import be.helmo.myscout.presenters.viewmodel.PhaseListViewModel
import be.helmo.myscout.repositories.IImageRepository
import be.helmo.myscout.view.interfaces.IPhaseRecyclerCallbackPresenter
import be.helmo.myscout.view.interfaces.IPhasesSelectPhaseCallback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class PhasePresenter(var myScoutRepository: MyScoutRepository, var imageRepository: IImageRepository) : IPhaseRecyclerCallbackPresenter, IPhasesSelectPhaseCallback {
    var phaseList: ArrayList<Phase> = ArrayList() //liste phase
    var phaseViewModels: ArrayList<PhaseListViewModel> = ArrayList() //list phase ViewModels

    var recyclerCallback: IPhaseRecyclerCallback? = null
    var selectsPhaseCallback: ISelectPhaseCallback? = null

    var startTime: Date? = null

    fun getPhases(meetingId: UUID) {
        GlobalScope.launch {
            myScoutRepository.getPhases(meetingId)?.take(1)?.collect { phases ->
                var meeting = myScoutRepository.getMeeting(meetingId)
                startTime = meeting?.startDate
                for (i in 0 until phases?.size!!) {
                    phases[i]?.let { phaseList.add(it) }
                        phaseViewModels.add(
                            PhaseListViewModel(
                                "Phase ${i + 1}",
                                phases[i]?.duration.toString(),
                                phases[i]?.description
                            )
                        )
                    startTime = getRightTime(startTime!!, phases[i]?.duration.toString())
                    //recylcerCallback?.onPhaseDataAdd(phaseViewModels.size)
                }
            }
        }
    }

    fun getRightTime(startTime: Date, duration: String): Date {
        val time = startTime.time
        val minutes = duration.split(":")[1].toInt()
        val durationTime = minutes * 60 * 1000
        return Date(time + durationTime)
    }

    override fun setPhaseListCallback(iPhaseListCallback: IPhaseRecyclerCallback?) {
        recyclerCallback = iPhaseListCallback
    }

    override fun saveImage(imageToSave: Bitmap, phaseId: UUID) : Uri {
        return imageRepository.createDirectoryAndSaveImage(imageToSave, phaseId.toString())
    }

    override fun onBindPhaseRowViewAtPosition(position: Int, rowView: IPhaseRowView) {
        val phase = phaseViewModels[position]
        rowView.setTitle(phase.s)
        rowView.setDuration(phase.duration)
        rowView.setDescription(phase.description)
    }

    override fun getPhaseRowsCount(): Int {
        return phaseViewModels.size
    }

    override fun modifyPhase(
        uuid: UUID,
        name: String,
        resume: String,
        duration: Long
    ) {
        phaseList.forEachIndexed{ index, phase ->
            if (phase.id == uuid) {
                phase.name = name
                phase.description = resume
                phase.duration = duration
                myScoutRepository.updatePhase(phase)
            }
        }
    }

    override fun addPhase(name: String, duration: Long, resume: String) {
        val phase = Phase(UUID.randomUUID(), name, resume, duration, "", false)
        myScoutRepository.insertPhase(phase)
        phaseList.add(phase)

        GlobalScope.launch {
            phaseViewModels.add(PhaseListViewModel(name, duration.toString(), resume))
            recyclerCallback?.onPhaseDataAdd(phaseViewModels.size)
        }
    }

    override fun removePhase(swipeItemPosition: Int) {
        myScoutRepository.deletePhase(phaseList[swipeItemPosition])
        phaseList.removeAt(swipeItemPosition)
        phaseViewModels.removeAt(swipeItemPosition)
    }

    override fun goToPhase(position: Int) {
        selectsPhaseCallback?.onSelectedPhase(phaseList[position], imageRepository.getImages(phaseList[position].id.toString()))
    }

    override fun setSelectPhaseCallback(iSelectPhaseCallback: ISelectPhaseCallback?) {
        selectsPhaseCallback = iSelectPhaseCallback
    }

}