package be.helmo.myscout.database;

import androidx.room.TypeConverter;

import java.util.Date;
import java.util.UUID;

public class MeetingReportTypeConverters {

    @TypeConverter
    public String fromUuid(UUID uuid) {
        return uuid.toString();
    }

    @TypeConverter
    public UUID toUuid(String uuid) {
        return UUID.fromString(uuid);
    }

    @TypeConverter
    public long fromDate(Date date) {
        return date.getTime();
    }

    @TypeConverter
    public Date toDate(long date) {
        return new Date(date);
    }
}
