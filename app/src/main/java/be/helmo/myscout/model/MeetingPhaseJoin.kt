package be.helmo.myscout.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.*


@Entity(
    primaryKeys = [ "meetingId", "phaseId" ],
    foreignKeys = [ForeignKey(
        entity = Meeting::class,
        childColumns = ["meetingId"],
        parentColumns = ["id"],
        onDelete = ForeignKey.CASCADE),
        ForeignKey(
            entity = Phase::class,
            childColumns = ["phaseId"],
            parentColumns = ["id"])],
    indices = [
        Index(value = ["phaseId"])
    ])

//@Entity(primaryKeys= [ "meetingId", "phaseId" ] )
data class MeetingPhaseJoin(
    @NonNull
    val mpJoinId: UUID,

    @NonNull
    val meetingId: UUID,

    @NonNull
    val phaseId: UUID
)