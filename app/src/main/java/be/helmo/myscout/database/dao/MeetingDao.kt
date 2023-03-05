package be.helmo.myscout.database.dao

import androidx.room.*
import be.helmo.myscout.model.Meeting
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface MeetingDao {
    @get:Query("SELECT * FROM Meeting")
    val meetings: Flow<List<Meeting?>?>?

    @Insert
    fun insert(meeting: Meeting?)

    @Update
    fun update(meeting: Meeting?)

    @Delete
    fun delete(meeting: Meeting?)

}