package be.helmo.myscout.presenters

import be.helmo.myscout.database.repository.MyScoutRepository
import be.helmo.myscout.factory.interfaces.IPhaseRecyclerCallback
import be.helmo.myscout.factory.interfaces.ISelectPhaseCallback
import be.helmo.myscout.model.Phase
import be.helmo.myscout.presenters.viewmodel.PhaseViewModel
import be.helmo.myscout.view.interfaces.IPhaseRecyclerCallbackPresenter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class PhasePresenter(var myScoutRepository: MyScoutRepository) : IPhaseRecyclerCallbackPresenter {
    var phaseList: ArrayList<Phase> = ArrayList<Phase>() //liste meetings
    var phaseViewModels: ArrayList<PhaseViewModel> = ArrayList<PhaseViewModel>() //list meetings ViewModels

    var recylcerCallback: IPhaseRecyclerCallback? = null
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
                            PhaseViewModel(
                                "Phase ${i + 1}",
                                phases[i]?.duration.toString(),
                                phases[i]?.description
                            )
                        )
                    startTime = getRightTime(startTime!!, phases[i]?.duration.toString())
                    recylcerCallback?.onPhaseDataAdd(phaseViewModels.size)
                }
            }
        }
    }

    private fun getRightTime(startTime: Date, duration: String): Date {
        val time = startTime.time
        val minutes = duration.split(":")[1].toInt()
        val durationTime = minutes * 60 * 1000
        return Date(time + durationTime)
    }

    override fun onBindPhaseRowViewAtPosition(position: Int, rowView: IPhaseRowView) {
        val phase = phaseViewModels[position]
        rowView.setTitle(phase.title)
        rowView.setDuration(phase.duration)
        rowView.setDescription(phase.description)
    }

    override fun getPhaseRowsCount(): Int {
        TODO("Not yet implemented")
    }

    override fun addPhase(during: String, description: String, images: String) {
        TODO("Not yet implemented")
    }

    override fun removePhase(swipeItemPosition: Int) {
        TODO("Not yet implemented")
    }

    override fun goToPhase(position: Int) {
        TODO("Not yet implemented")
    }


}