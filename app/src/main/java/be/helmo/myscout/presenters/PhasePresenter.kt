package be.helmo.myscout.presenters

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import be.helmo.myscout.database.repository.MyScoutRepository
import be.helmo.myscout.factory.interfaces.IPhaseRecyclerCallback
import be.helmo.myscout.factory.interfaces.ISelectPhaseCallback
import be.helmo.myscout.model.MeetingPhaseJoin
import be.helmo.myscout.model.Phase
import be.helmo.myscout.presenters.interfaces.IPhaseRowView
import be.helmo.myscout.presenters.viewmodel.PhaseListViewModel
import be.helmo.myscout.presenters.viewmodel.PhaseViewModel
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

    var meetingId: UUID? = null

    override fun getPhases(meetingId: UUID, startDate: Date) {
        this.meetingId = meetingId
        this.startTime = startDate
        GlobalScope.launch {
            phaseViewModels.clear()
            phaseList.clear()
            myScoutRepository.getPhases(meetingId)?.take(1)?.collect { phases ->
                for (i in 0 until phases?.size!!) {
                    phases[i]?.let { phaseList.add(it) }
                        phaseViewModels.add(
                            PhaseListViewModel(
                                "Phase ${i + 1}",
                                phases[i]?.duration.toString(),
                                phases[i]?.description
                            )
                        )

                    startTime = getRightTime(startTime!!, phases[i]?.duration!!.toInt())
                    recyclerCallback?.onPhaseDataAdd(phaseViewModels.size)
                }
            }
        }
    }

    fun getRightTime(startTime: Date, duration: Int): Date {
        val time = startTime.time
        //val minutes = duration.split(":")[1].toInt()
        val durationTime = duration * 60 * 1000
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
        description: String,
        duration: Long,
        favorite: Boolean
    )
    {
        phaseList.forEachIndexed{ index, phase ->
            if (phase.id == uuid) {
                phase.name = name
                phase.description = description
                phase.duration = duration
                phase.favorite = favorite
                myScoutRepository.updatePhase(phase)
            }
            GlobalScope.launch {
                phaseViewModels[index].s = phase.name
                phaseViewModels[index].duration = phase.duration.toString()
                phaseViewModels[index].description = phase.description
            }
        }
    }


    override fun addPhase(uuid: UUID, name: String, duration: Long, description: String, favorite: Boolean) {
        val phase = Phase(uuid, name, description, duration, "", favorite)
        val meetingPhaseJoin = MeetingPhaseJoin(UUID.randomUUID(), meetingId!!, phase.id)
        myScoutRepository.insertPhase(phase)
        myScoutRepository.insertMeetingPhaseJoin(meetingPhaseJoin)
        phaseList.add(phase)

        GlobalScope.launch {
            phaseViewModels.add(PhaseListViewModel(name, duration.toString(), description))
            recyclerCallback?.onPhaseDataAdd(phaseViewModels.size)
        }
    }

    override fun removePhase(uuid: UUID) {
        val index = phaseList.indexOf(phaseList.find { it.id == uuid })
        myScoutRepository.deletePhase(phaseList[index])
        phaseList.removeAt(index)
        phaseViewModels.removeAt(index)

    }

    override fun removePhaseAt(index: Int) {
        myScoutRepository.deletePhase(phaseList[index])
        phaseList.removeAt(index)
        phaseViewModels.removeAt(index)

    }

    override fun movePhase(position: Int, direction: Int) {
        Collections.swap(phaseViewModels, position, position + direction)
    }

    override fun goToPhase(position: Int) {
        val currentPhase = phaseList[position]
        val phaseViewModel = PhaseViewModel(currentPhase.id, currentPhase.name, currentPhase.description, currentPhase.duration, currentPhase.notice, currentPhase.favorite)
        Log.d("test : ", phaseList[position].id.toString())
        Log.d("test : ", imageRepository.getImages(phaseList[position].id.toString()).size.toString())
        selectsPhaseCallback?.onSelectedPhase(phaseViewModel, imageRepository.getImages(phaseList[position].id.toString())) //todo passer viewModel phase ?
    }

    override fun setSelectPhaseCallback(iSelectPhaseCallback: ISelectPhaseCallback?) {
        selectsPhaseCallback = iSelectPhaseCallback
    }

}