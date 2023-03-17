package be.helmo.myscout.view.interfaces

import android.graphics.Bitmap
import android.net.Uri
import java.util.*

interface IPhasePresenter : IPhaseRecyclerCallbackPresenter, IPhasesSelectPhaseCallback, IFavoritePhaseRecyclerCallbackPresenter {

    fun getPhases(meetingId: UUID, startDate: Date)

    fun saveImage(imageToSave: Bitmap, phaseId: UUID) : Uri

    fun addPhase(uuid: UUID,
                 name: String,
                 duration: Long,
                 description: String,
                 favorite: Boolean)
    fun modifyPhase(uuid: UUID,
                    name: String,
                    description: String,
                    duration: Long,
                    favorite: Boolean)
    fun removePhase(uuid: UUID)

    fun deleteImage(imageUri: Uri?)

    fun removePhasesImages(currentMeetingUUID: UUID)

    fun isPhaseNotFavorite(currentPhaseUUID: UUID): Boolean

}