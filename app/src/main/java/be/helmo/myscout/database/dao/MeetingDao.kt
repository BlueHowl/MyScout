package be.helmo.myscout.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.model.Phase
import java.util.*

@Dao
interface MeetingDao {
    @get:Query("SELECT * FROM Meeting")
    val meetings: LiveData<List<Meeting?>?>?

    @Insert
    fun insert(meeting: Meeting?)

    @Update
    fun update(meeting: Meeting?)

    @Delete
    fun delete(meeting: Meeting?)
}