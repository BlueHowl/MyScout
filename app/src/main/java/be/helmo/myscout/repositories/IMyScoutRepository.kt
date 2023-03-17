package be.helmo.myscout.repositories

import be.helmo.myscout.model.Meeting
import be.helmo.myscout.model.MeetingPhaseJoin
import be.helmo.myscout.model.Phase
import kotlinx.coroutines.flow.Flow
import java.util.*

interface IMyScoutRepository {
    val meetings: Flow<List<Meeting?>?>?
    fun insertMeeting(meeting: Meeting?)
    fun updateMeeting(meeting: Meeting?)
    fun deleteMeeting(meeting: Meeting?)

    fun getPhases(meetingUUID: UUID?): Flow<List<Phase?>?>?
    val favoritePhases: Flow<List<Phase?>?>?
    fun getPhase(phaseUUID: UUID?): Flow<Phase?>?
    fun insertPhase(phase: Phase?)
    fun updatePhase(phase: Phase?)
    fun deletePhase(phase: Phase?)
    fun insertMeetingPhaseJoin(meetingPhaseJoin: MeetingPhaseJoin)
}