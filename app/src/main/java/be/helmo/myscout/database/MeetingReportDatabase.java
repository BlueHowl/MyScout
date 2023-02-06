package be.helmo.myscout.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import be.helmo.myscout.database.dao.MeetingDao;
import be.helmo.myscout.model.Meeting;

@Database(entities = {Meeting.class}, version = 1, exportSchema = false)
@TypeConverters({MeetingReportTypeConverters.class})
public abstract class MeetingReportDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "scout_meetings_database";
    private static MeetingReportDatabase instance;

    public abstract MeetingDao meetingDao();

    public static void initDatabase(Context context) {
        if(instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext(), MeetingReportDatabase.class, DATABASE_NAME).build();
    }

    public static MeetingReportDatabase getInstance() {
        if(instance == null)
            throw new IllegalStateException("Scout meetings database must be initialized");
        return  instance;
    }

    public static void disconnectDatabase() {
        instance = null;
    }
}
