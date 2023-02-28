package be.helmo.myscout.database.repository

import androidx.lifecycle.LiveData
import be.helmo.myscout.database.MyScoutDatabase
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.model.Phase
import java.util.*
import java.util.concurrent.Executors

class MyScoutRepository {//private constructor() {
    val meetingDao = MyScoutDatabase.getInstance()?.meetingDao()
    val phaseDao = MyScoutDatabase.getInstance()?.phaseDao()

    val executor = Executors.newSingleThreadExecutor()

    //Meeting
    val meetings: LiveData<List<Meeting?>?>?
        get() = meetingDao?.meetings

    fun insertMeeting(meeting: Meeting?) {
        executor.execute { meetingDao?.insert(meeting) }
    }

    fun updateMeeting(meeting: Meeting?) {
        executor.execute { meetingDao?.update(meeting) }
    }

    fun deleteMeeting(meeting: Meeting?) {
        executor.execute { meetingDao?.delete(meeting) }
    }

    //Phase
    fun getPhases(meetingUUID: UUID?): LiveData<List<Phase?>?>? {
        return phaseDao?.getPhases(meetingUUID)
    }

    fun getPhase(phaseUUID: UUID?): LiveData<Phase?>? {
        return phaseDao?.getPhase(phaseUUID)
    }

    fun insertPhase(phase: Phase?) {
        executor.execute { phaseDao?.insert(phase) }
    }

    fun updatePhase(phase: Phase?) {
        executor.execute { phaseDao?.update(phase) }
    }

    fun deletePhase(phase: Phase?) {
        executor.execute { phaseDao?.delete(phase) }
    }

    companion object {
        var instance: MyScoutRepository? = null
            get() {
                if (field == null) field = MyScoutRepository()
                return field
            }
            private set
    }
}