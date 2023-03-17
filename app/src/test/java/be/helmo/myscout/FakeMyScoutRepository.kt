package be.helmo.myscout

import be.helmo.myscout.model.Meeting
import be.helmo.myscout.model.MeetingPhaseJoin
import be.helmo.myscout.model.Phase
import be.helmo.myscout.repositories.IMyScoutRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

class FakeMyScoutRepository : IMyScoutRepository {
    var meetingsList = mutableListOf<Meeting?>()
    var phasesList = mutableListOf<Phase?>()
    var meetingPhaseJoinList = mutableListOf<MeetingPhaseJoin?>()

    override val meetings: Flow<List<Meeting?>?>
        get() = TODO("Not yet implemented")

    override fun insertMeeting(meeting: Meeting?) {
        meetingsList.add(meeting)
    }

    override fun updateMeeting(meeting: Meeting?) {
        meetingsList[meetingsList.indexOf(meeting)].apply {
            this?.id = meeting?.id!!
            this?.description = meeting.description!!
            this?.endDate = meeting.endDate!!
            this?.startDate = meeting.startDate!!
            this?.startLocation = meeting.startLocation!!
            this?.endLocation = meeting.endLocation!!
            this?.rating = meeting.rating!!
            this?.story = meeting.story!!
        }
    }

    override fun deleteMeeting(meeting: Meeting?) {
        meetingsList.remove(meeting)
    }

    override fun getPhases(meetingUUID: UUID?): Flow<List<Phase?>?>? {
        TODO("no need to implement this")
    }

    override val favoritePhases: Flow<List<Phase?>?>?
        get() = TODO("no need to implement this")

    override fun getPhase(phaseUUID: UUID?): Flow<Phase?>? {
        return TODO("no need to implement this")
    }

    override fun insertPhase(phase: Phase?) {
        TODO("no need to implement this")
    }

    override fun updatePhase(phase: Phase?) {
        TODO("no need to implement this")
    }

    override fun deletePhase(phase: Phase?) {
        TODO("no need to implement this")
    }

    override fun insertMeetingPhaseJoin(meetingPhaseJoin: MeetingPhaseJoin) {
        TODO("no need to implement this")
    }
}