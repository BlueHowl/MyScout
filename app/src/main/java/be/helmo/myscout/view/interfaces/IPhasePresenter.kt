package be.helmo.myscout.view.interfaces

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import be.helmo.myscout.MainActivity
import be.helmo.myscout.presenters.interfaces.IPhaseRowView
import java.util.*

interface IPhasePresenter {

    fun getPhases(meetingId: UUID, startDate: Date)

    fun saveImage(imageToSave: Bitmap, phaseId: UUID) : Uri

    fun onBindPhaseRowViewAtPosition(position: Int, rowView: IPhaseRowView)

    fun getPhaseRowsCount() : Int

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

    fun removePhaseAt(index: Int)

    fun movePhase(position: Int, toPosition: Int)

    fun goToPhase(position: Int)

    fun deleteImage(imageUri: Uri?)

    fun removePhasesImages(currentMeetingUUID: UUID)

    fun getFavoritePhases(meetingId: UUID)
    fun isPhaseNotFavorite(currentPhaseUUID: UUID): Boolean

}