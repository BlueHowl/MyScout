package be.helmo.myscout.presenters.viewmodel

import com.google.android.gms.maps.model.LatLng
import java.util.*

data class MeetingViewModel(
    val meetId: UUID, //passe le id qd mm ?
    val startDate: String?,
    val endDate: String?,
    val startAddress: String?,
    val endAddress: String?,
    val startLocation: LatLng?,
    val endLocation: LatLng?,
    val description: String?,
    val story: String?
)
