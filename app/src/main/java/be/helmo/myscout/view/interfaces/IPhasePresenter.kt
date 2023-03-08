package be.helmo.myscout.view.interfaces

import android.graphics.Bitmap
import android.net.Uri
import be.helmo.myscout.presenters.interfaces.IPhaseRowView
import java.util.*

interface IPhasePresenter : IPhaseRecyclerCallbackPresenter {

    fun getPhases(meetingId: UUID, startDate: Date)

    fun saveImage(imageToSave: Bitmap, phaseId: UUID) : Uri

    fun onBindPhaseRowViewAtPosition(position: Int, rowView: IPhaseRowView)

    fun getPhaseRowsCount() : Int

    fun addPhase(name: String,
                 duration: Long,
                 description: String,
                 favorite: Boolean)
    fun modifyPhase(uuid: UUID,
                    name: String,
                    description: String,
                    duration: Long,
                    favorite: Boolean)
    fun removePhase(swipeItemPosition: Int)

    fun goToPhase(position: Int)

}