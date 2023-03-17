package be.helmo.myscout.repositories

import android.graphics.Bitmap
import android.net.Uri
import be.helmo.myscout.model.Phase
import kotlinx.coroutines.flow.Flow
import kotlin.collections.ArrayList

interface IImageRepository {

    fun createDirectoryAndSaveImage(imageToSave: Bitmap, directoryName: String) : Uri

    fun getImages(imagesDirectory: String): ArrayList<Uri>

    fun deleteImage(imageToDelete: Uri?)
    fun deletePhaseImages(phase: Phase)
    fun deletePhasesImages(phases: List<Phase?>?)
}