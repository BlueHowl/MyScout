package be.helmo.myscout.database

import android.content.Context
import androidx.room.*
import androidx.room.Room.databaseBuilder
import androidx.room.util.TableInfo
import be.helmo.myscout.database.dao.MeetingDao
import be.helmo.myscout.database.dao.PhaseDao
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.model.MeetingPhaseJoin
import be.helmo.myscout.model.Phase
import java.util.UUID

@Database(entities = [Meeting::class, Phase::class, MeetingPhaseJoin::class], version = 1, exportSchema = false)
@TypeConverters(MyScoutTypeConverters::class)
abstract class MyScoutDatabase : RoomDatabase() {
    abstract fun meetingDao(): MeetingDao
    abstract fun phaseDao(): PhaseDao

    companion object {
        private const val DATABASE_NAME = "scout_meetings_database"
        private var instance: MyScoutDatabase? = null

        fun initDatabase(context: Context) {
            if (instance == null) instance = databaseBuilder(
                context,
                MyScoutDatabase::class.java, DATABASE_NAME
            ).build()
        }

        fun getInstance(): MyScoutDatabase? {
            checkNotNull(instance) { "Scout meetings database must be initialized" }
            return instance
        }

        fun disconnectDatabase() {
            instance = null
        }
    }
}