package be.helmo.myscout.database.dao

import androidx.room.*
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.model.Phase
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface MeetingDao {

    @get:Query("SELECT * FROM Meeting ORDER BY startDate")
    val meetings: Flow<List<Meeting?>?>?

    @Query("SELECT id, description, story, startDate, endDate, startLocation, endLocation, rating FROM Meeting " +
            "WHERE id = (:uuid)")
    fun getMeeting(uuid: UUID?): Flow<Meeting?>?

    @Insert
    fun insert(meeting: Meeting?)

    @Update
    fun update(meeting: Meeting?)

    @Delete
    fun delete(meeting: Meeting?)

}