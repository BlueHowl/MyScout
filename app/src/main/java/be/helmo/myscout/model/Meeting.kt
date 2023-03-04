package be.helmo.myscout.model

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.util.*

@Entity
data class Meeting (
    @PrimaryKey
    val id: UUID,

    var description: String?,

    var story: String?,

    var startDate: Date,

    var endDate: Date,

    var startLocation: LatLng,

    var endLocation: LatLng
)