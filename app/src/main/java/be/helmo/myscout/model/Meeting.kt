package be.helmo.myscout.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.util.*

@Entity(tableName = "meeting")
data class Meeting (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: UUID,

    @ColumnInfo(name = "description")
    var description: String?,

    @ColumnInfo(name = "story")
    var story: String?,

    @ColumnInfo(name = "startDate")
    var startDate: Date,

    @ColumnInfo(name = "endDate")
    var endDate: Date,

    @ColumnInfo(name = "startLocation")
    var startLocation: LatLng,

    @ColumnInfo(name = "endLocation")
    var endLocation: LatLng,

    @ColumnInfo(name = "rating")
    var rating: Float?
)