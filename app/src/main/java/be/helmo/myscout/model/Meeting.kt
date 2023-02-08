package be.helmo.myscout.model

import android.location.Location
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Meeting (
    @PrimaryKey
    @NonNull
    val id: UUID,

    var description: String?,

    var story: String?,

    @NonNull
    var startDate: Date,

    @NonNull
    var endDate: Date,

    @NonNull
    var startLocation: Location,

    @NonNull
    var endLocation: Location
)