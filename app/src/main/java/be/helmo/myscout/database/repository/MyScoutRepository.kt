package be.helmo.myscout.database.repository

import android.util.Log
import be.helmo.myscout.database.MyScoutDatabase
import be.helmo.myscout.imageRepository.ImageRepository
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.model.MeetingPhaseJoin
import be.helmo.myscout.model.Phase
import be.helmo.myscout.repositories.IImageRepository
import be.helmo.myscout.repositories.IMyScoutRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Executors

class MyScoutRepository(var imageRepository: IImageRepository) : IMyScoutRepository {//private constructor() {
    val meetingDao = MyScoutDatabase.getInstance()?.meetingDao()
    val phaseDao = MyScoutDatabase.getInstance()?.phaseDao()

    val executor = Executors.newSingleThreadExecutor()

    //Meeting
    override val meetings: Flow<List<Meeting?>?>?
        get() = meetingDao?.meetings

    override fun insertMeeting(meeting: Meeting?) {
        Log.d("meetingDao", meetingDao?.meetings.toString()) //null wtf
        executor.execute { meetingDao?.insert(meeting) }
    }

    override fun updateMeeting(meeting: Meeting?) {
        executor.execute { meetingDao?.update(meeting) }
    }

    override fun deleteMeeting(meeting: Meeting?) {
        GlobalScope.launch {
            phaseDao?.getPhases(meeting?.id)?.take(1)?.collect{ phases ->
                for (i in 0 until phases?.size!!) {
                    if(phases[i]?.favorite == false){
                        imageRepository.deletePhaseImages(phases[i]!!)
                    }
                }
                executor.execute { meetingDao?.delete(meeting) }
            }
        }
    }

    //Phase
    override fun getPhases(meetingUUID: UUID?): Flow<List<Phase?>?>? {
        return phaseDao?.getPhases(meetingUUID)
    }

    override val favoritePhases: Flow<List<Phase?>?>?
        get() = phaseDao?.favoritePhases

    override fun getPhase(phaseUUID: UUID?): Flow<Phase?>? {
        return phaseDao?.getPhase(phaseUUID)
    }

    override fun insertPhase(phase: Phase?) {
        executor.execute { phaseDao?.insert(phase) }
    }

    override fun updatePhase(phase: Phase?) {
        executor.execute { phaseDao?.update(phase) }
    }

    override fun deletePhase(phase: Phase?) {
        executor.execute { phaseDao?.delete(phase?.id) }
    }

    override fun insertMeetingPhaseJoin(meetingPhaseJoin: MeetingPhaseJoin) {
        executor.execute { phaseDao?.insert(meetingPhaseJoin) }
    }
}
