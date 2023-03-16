package be.helmo.myscout.database.repository

import android.util.Log
import be.helmo.myscout.database.MyScoutDatabase
import be.helmo.myscout.imageRepository.ImageRepository
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.model.MeetingPhaseJoin
import be.helmo.myscout.model.Phase
import be.helmo.myscout.repositories.IImageRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Executors

class MyScoutRepository(var imageRepository: IImageRepository) {//private constructor() {
    val meetingDao = MyScoutDatabase.getInstance()?.meetingDao()
    val phaseDao = MyScoutDatabase.getInstance()?.phaseDao()

    val executor = Executors.newSingleThreadExecutor()

    //Meeting
    val meetings: Flow<List<Meeting?>?>?
        get() = meetingDao?.meetings

    fun insertMeeting(meeting: Meeting?) {
        Log.d("meetingDao", meetingDao?.meetings.toString()) //null wtf
        executor.execute { meetingDao?.insert(meeting) }
    }

    fun updateMeeting(meeting: Meeting?) {
        executor.execute { meetingDao?.update(meeting) }
    }

    fun deleteMeeting(meeting: Meeting?) {
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
    fun getPhases(meetingUUID: UUID?): Flow<List<Phase?>?>? {
        return phaseDao?.getPhases(meetingUUID)
    }

    val favoritePhases: Flow<List<Phase?>?>?
        get() = phaseDao?.favoritePhases

    fun getPhase(phaseUUID: UUID?): Flow<Phase?>? {
        return phaseDao?.getPhase(phaseUUID)
    }

    fun insertPhase(phase: Phase?) {
        executor.execute { phaseDao?.insert(phase) }
    }

    fun updatePhase(phase: Phase?) {
        executor.execute { phaseDao?.update(phase) }
    }

    fun deletePhase(phase: Phase?) {
        executor.execute { phaseDao?.delete(phase?.id) }
    }

    fun insertMeetingPhaseJoin(meetingPhaseJoin: MeetingPhaseJoin) {
        executor.execute { phaseDao?.insert(meetingPhaseJoin) }
    }
}