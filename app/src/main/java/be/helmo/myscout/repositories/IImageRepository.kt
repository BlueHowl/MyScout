package be.helmo.myscout.repositories

import android.graphics.Bitmap
import android.net.Uri

interface IImageRepository {

    fun createDirectoryAndSaveImage(imageToSave: Bitmap, directoryName: String) : Uri

    fun getImages(imagesDirectory: String): ArrayList<Uri>

}