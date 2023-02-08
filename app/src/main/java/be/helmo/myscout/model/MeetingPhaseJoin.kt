package be.helmo.myscout.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.*


@Entity(primaryKeys = [ "meetingId", "phaseId" ],
    foreignKeys = [ForeignKey(
        entity = Meeting::class,
        childColumns = ["meetingId"],
        parentColumns = ["id"]),
        ForeignKey(
            entity = Phase::class,
            childColumns = ["phaseId"],
            parentColumns = ["id"]
        )])

//@Entity(primaryKeys= [ "meetingId", "phaseId" ] )
data class MeetingPhaseJoin(
    @NonNull
    val meetingId: UUID,

    @NonNull
    val phaseId: UUID
)