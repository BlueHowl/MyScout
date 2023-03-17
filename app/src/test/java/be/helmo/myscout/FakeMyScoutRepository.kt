package be.helmo.myscout

import be.helmo.myscout.database.repository.MyScoutRepository
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.model.MeetingPhaseJoin
import be.helmo.myscout.model.Phase
import be.helmo.myscout.repositories.IMyScoutRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

class FakeMyScoutRepository : IMyScoutRepository {
    override val meetings: Flow<List<Meeting?>?>
        get() = TODO("Not yet implemented")

    override fun insertMeeting(meeting: Meeting?) {
        TODO("Not yet implemented")
    }

    override fun updateMeeting(meeting: Meeting?) {
        TODO("Not yet implemented")
    }

    override fun deleteMeeting(meeting: Meeting?) {
        TODO("Not yet implemented")
    }

    override fun getPhases(meetingUUID: UUID?): Flow<List<Phase?>?>? {
        TODO("Not yet implemented")
    }

    override val favoritePhases: Flow<List<Phase?>?>?
        get() = TODO("Not yet implemented")

    override fun getPhase(phaseUUID: UUID?): Flow<Phase?>? {
        TODO("Not yet implemented")
    }

    override fun insertPhase(phase: Phase?) {
        TODO("Not yet implemented")
    }

    override fun updatePhase(phase: Phase?) {
        TODO("Not yet implemented")
    }

    override fun deletePhase(phase: Phase?) {
        TODO("Not yet implemented")
    }

    override fun insertMeetingPhaseJoin(meetingPhaseJoin: MeetingPhaseJoin) {
        TODO("Not yet implemented")
    }
}