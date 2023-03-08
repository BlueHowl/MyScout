package be.helmo.myscout.view.interfaces

import android.graphics.Bitmap
import android.net.Uri
import be.helmo.myscout.presenters.interfaces.IPhaseRowView
import java.util.UUID

interface IPhasePresenter {

    fun saveImage(imageToSave: Bitmap, phaseId: UUID) : Uri

    fun onBindPhaseRowViewAtPosition(position: Int, rowView: IPhaseRowView)

    fun getPhaseRowsCount() : Int

    fun addPhase(name: String,
                 duration: Long,
                 description: String)
    fun modifyPhase(uuid: UUID,
                    name: String,
                    resume: String,
                    duration: Long)
    fun removePhase(swipeItemPosition: Int)

    fun goToPhase(position: Int)

}