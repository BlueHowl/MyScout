package be.helmo.myscout.database.repository;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import be.helmo.myscout.database.MeetingReportDatabase;
import be.helmo.myscout.database.dao.MeetingDao;
import be.helmo.myscout.model.Meeting;

public class MeetingRepository {

    private static MeetingRepository instance;

    private final MeetingDao meetingDao = MeetingReportDatabase.getInstance().meetingDao();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private MeetingRepository() {}

    public LiveData<List<Meeting>> getMeetings() {
        return meetingDao.getMeetings();
    }

    public LiveData<Meeting> getMeeting(UUID uuid) {
        return meetingDao.getMeeting(uuid);
    }

    public void insertMeeting(final Meeting meeting) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                meetingDao.insert(meeting);
            }
        });
    }

    public void updateMeeting(final Meeting meeting) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                meetingDao.update(meeting);
            }
        });
    }

    public static MeetingRepository getInstance() {
        if(instance == null)
            instance = new MeetingRepository();
        return instance;
    }
}
