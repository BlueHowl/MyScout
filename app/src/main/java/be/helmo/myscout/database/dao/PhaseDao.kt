package be.helmo.myscout.database.dao

import androidx.room.*
import be.helmo.myscout.model.Phase
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface PhaseDao {
    @Query("SELECT P.id, P.description, P.duration, P.notice, P.favorite FROM " +
            "Phase AS P " +
            "JOIN MeetingPhaseJoin AS MPJ ON P.id = MPJ.phaseId " +
            "JOIN Meeting AS M ON M.id = MPJ.meetingId " +
            "WHERE M.id = (:uuid)")
    fun getPhases(uuid: UUID?): Flow<List<Phase?>?>?

    @Query("SELECT id, name, description, duration, notice, favorite FROM Phase " +
            "WHERE id = (:uuid)")
    fun getPhase(uuid: UUID?): Flow<Phase?>?

    @Insert
    fun insert(phase: Phase?)

    @Update
    fun update(phase: Phase?)

    @Delete
    fun delete(phase: Phase?)
}