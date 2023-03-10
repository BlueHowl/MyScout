package be.helmo.myscout.presenters.viewmodel

import android.media.Rating
import java.util.UUID

data class PhaseViewModel (
    val phaseId: UUID?,
    val name: String?,
    val description: String?,
    val duration: Long?,
    val notice: String?,
    val favorite: Boolean?
)