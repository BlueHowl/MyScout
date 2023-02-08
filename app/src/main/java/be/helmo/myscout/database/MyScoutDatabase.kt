package be.helmo.myscout.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import be.helmo.myscout.database.dao.MeetingDao
import be.helmo.myscout.database.dao.PhaseDao
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.model.MeetingPhaseJoin
import be.helmo.myscout.model.Phase

@Database(entities = [Meeting::class, Phase::class, MeetingPhaseJoin::class], version = 1, exportSchema = false)
@TypeConverters(MyScoutTypeConverters::class)
abstract class MyScoutDatabase : RoomDatabase() {
    abstract fun meetingDao(): MeetingDao?
    abstract fun phaseDao(): PhaseDao?

    companion object {
        private const val DATABASE_NAME = "scout_meetings_database"
        private var instance: MyScoutDatabase? = null
        fun initDatabase(context: Context) {
            if (instance == null) instance = databaseBuilder(
                context.applicationContext,
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