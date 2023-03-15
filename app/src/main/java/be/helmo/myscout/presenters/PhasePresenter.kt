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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min

class PhasePresenter(var myScoutRepository: MyScoutRepository, var imageRepository: IImageRepository) : IPhaseRecyclerCallbackPresenter, IPhasesSelectPhaseCallback {
    var phaseList: ArrayList<Phase> = ArrayList() //liste phase
    var phaseListViewModels: ArrayList<PhaseListViewModel> = ArrayList() //list phase ViewModels

    var recyclerCallback: IPhaseRecyclerCallback? = null
    var selectsPhaseCallback: ISelectPhaseCallback? = null

    lateinit var currentStartDate: Date
    lateinit var tempTime: Date

    var meetingId: UUID? = null

    val sdf: SimpleDateFormat = SimpleDateFormat("EEE hh:mm")

    override fun getPhases(meetingId: UUID, startDate: Date) {
        this.meetingId = meetingId
        this.currentStartDate = startDate
        this.tempTime = startDate
        phaseListViewModels.clear()
        phaseList.clear()
        GlobalScope.launch {


            myScoutRepository.getPhases(meetingId)?.take(1)?.collect { phases ->
                for (i in 0 until phases?.size!!) {
                    phaseList.add(phases[i]!!)
                    phaseListViewModels.add(
                        PhaseListViewModel(
                            phaseListViewModels.size+1,
                            phases[i]?.name,
                            phases[i]?.duration,
                            sdf.format(tempTime),
                            phases[i]?.description
                        )
                    )

                    tempTime = getRightTime(tempTime, phases[i]?.duration!!.toInt())
                    recyclerCallback?.onPhaseDataChanged(phaseListViewModels.size) //todo pq notifyInsert ne fonctionne pas
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
        val phase = phaseListViewModels[position]
        rowView.setTitle(String.format("#%d %s", phase.num, phase.s))
        rowView.setStartTime(String.format("%s(%dmin)", phase.startTime, phase.duration))
        rowView.setDescription(phase.description)
    }

    override fun getPhaseRowsCount(): Int {
        return phaseListViewModels.size
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
                phaseListViewModels[index].s = phase.name
                phaseListViewModels[index].duration = phase.duration
                phaseListViewModels[index].description = phase.description
            }
        }
    }


    override fun addPhase(uuid: UUID, name: String, duration: Long, description: String, favorite: Boolean) {
        val phase = Phase(uuid, phaseListViewModels.size+1, name, description, duration, "", favorite)
        val meetingPhaseJoin = MeetingPhaseJoin(UUID.randomUUID(), meetingId!!, phase.id)
        myScoutRepository.insertPhase(phase)
        myScoutRepository.insertMeetingPhaseJoin(meetingPhaseJoin)
        phaseList.add(phase)

        GlobalScope.launch {
            phaseListViewModels.add(PhaseListViewModel(phaseListViewModels.size+1, name, duration, sdf.format(getRightTime(tempTime, duration.toInt())), description))
            recyclerCallback?.onPhaseDataChanged(phaseListViewModels.size) //todo pq notifyInsert ne fonctionne pas
        }
    }

    override fun removePhase(uuid: UUID) {
        val index = phaseList.indexOf(phaseList.find { it.id == uuid })
        myScoutRepository.deletePhase(phaseList[index])
        imageRepository.deletePhaseImages(phaseList[index])
        phaseList.removeAt(index)
        phaseListViewModels.removeAt(index)

    }

    override fun removePhaseAt(index: Int) {
        myScoutRepository.deletePhase(phaseList[index])
        imageRepository.deletePhaseImages(phaseList[index])
        phaseList.removeAt(index)
        phaseListViewModels.removeAt(index)
    }

    override fun movePhase(position: Int, toPosition: Int) {
        Log.d("mouvement position phase", "$position   ->  $toPosition")

        phaseListViewModels.add(toPosition, phaseListViewModels.removeAt(position))
        phaseList.add(toPosition, phaseList.removeAt(position))

        //recalcul les phases
        var tempTime: Date? = null
        for(i in min(position, toPosition) .. max(position, toPosition)) {
            if(i-1 < 0) {
                tempTime = currentStartDate
            } else {
                tempTime = getRightTime(tempTime ?: currentStartDate, phaseListViewModels[i-1].duration!!.toInt())
            }

            phaseList[i].num = i+1
            myScoutRepository.updatePhase(phaseList[i]) //met à jour en bd

            phaseListViewModels[i].num = i+1
            phaseListViewModels[i].startTime = sdf.format(tempTime)
            recyclerCallback?.onPhaseDataChanged(i)
        }

    }

    override fun goToPhase(position: Int) {
        val currentPhase = phaseList[position]
        val phaseViewModel = PhaseViewModel(currentPhase.id, currentPhase.name, currentPhase.description, currentPhase.duration, currentPhase.notice, currentPhase.favorite)
        selectsPhaseCallback?.onSelectedPhase(phaseViewModel, imageRepository.getImages(phaseList[position].id.toString())) //todo passer viewModel phase ?
    }

    override fun deleteImage(imageUri: Uri?) {
        imageRepository.deleteImage(imageUri)
    }

    override fun removePhasesImages(currentMeetingUUID: UUID) {
        GlobalScope.launch {
        myScoutRepository.getPhases(currentMeetingUUID)?.take(1)?.collect { phases ->
                imageRepository.deletePhasesImages(phases)
            }
        }
    }

    override fun setSelectPhaseCallback(iSelectPhaseCallback: ISelectPhaseCallback?) {
        selectsPhaseCallback = iSelectPhaseCallback
    }

}