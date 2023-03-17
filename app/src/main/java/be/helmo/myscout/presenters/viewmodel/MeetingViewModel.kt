package be.helmo.myscout.presenters.viewmodel

import com.google.android.gms.maps.model.LatLng
import java.util.*

data class MeetingViewModel(
    val meetId: UUID, //passe le id qd mm ?
    var startDate: String?,
    var endDate: String?,
    var startAddress: String?,
    var endAddress: String?,
    var startLocation: LatLng?,
    var endLocation: LatLng?,
    var description: String?,
    var story: String?,
    var rating: Float?
)
