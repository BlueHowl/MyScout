package be.helmo.myscout.presenters.viewmodel

import java.util.*

data class MeetingViewModel(
    val meetId: UUID, //passe le id qd mm ?
    val startDate: String?,
    val endDate: String?,
    val startAddress: String?,
    val endAddress: String?,
    val description: String?,
    val story: String?
)
