package be.helmo.myscout.database.dao

import androidx.room.*
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.model.MeetingPhaseJoin
import be.helmo.myscout.model.Phase
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface PhaseDao {
    @Query("SELECT P.id, P.num, P.name, P.description, P.duration, P.notice, P.favorite FROM " +
            "Phase AS P " +
            "JOIN MeetingPhaseJoin AS MPJ ON P.id = MPJ.phaseId " +
            "JOIN Meeting AS M ON M.id = MPJ.meetingId " +
            "WHERE M.id = (:uuid) ORDER BY P.num")
    fun getPhases(uuid: UUID?): Flow<List<Phase?>?>?

    @get:Query("SELECT id, num, name, description, duration, notice, favorite FROM " +
                "Phase WHERE favorite = 1")
    val favoritePhases: Flow<List<Phase?>?>?

    @Query("SELECT id, num, name, description, duration, notice, favorite FROM Phase " +
            "WHERE id = (:uuid)")
    fun getPhase(uuid: UUID?): Flow<Phase?>?

    @Insert
    fun insert(phase: Phase?)

    @Update
    fun update(phase: Phase?)

    @Query("DELETE FROM MeetingPhaseJoin WHERE phaseId = (:uuid)")
    fun delete(uuid: UUID?)

    @Insert
    fun insert(meetingPhaseJoin: MeetingPhaseJoin?) //todo separate DAO ?
}