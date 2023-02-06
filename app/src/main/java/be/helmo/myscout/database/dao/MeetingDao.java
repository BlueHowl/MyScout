package be.helmo.myscout.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.UUID;

import be.helmo.myscout.model.Meeting;


@Dao
public interface MeetingDao {

    @Query("SELECT * FROM Meeting")
    LiveData<List<Meeting>> getMeetings();

    @Query("SELECT * FROM Meeting where id = (:uuid)")
    LiveData<Meeting> getMeeting(UUID uuid);

    @Insert
    void insert(Meeting meeting);

    @Update
    void update(Meeting meeting);
}
