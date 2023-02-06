package be.helmo.myscout.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

@Entity
public class Meeting {

    @PrimaryKey
    @NonNull
    private UUID id;
    private String description;
    private String story;
    private Date startDate;
    private Date endDate;

    private double[] startLocation;
    private double[] endLocation;


    public Meeting() {
        id = UUID.randomUUID();
        description = "";
        story = "";
        startDate = new Date();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /*TODO location*/
}
