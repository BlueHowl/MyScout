package be.helmo.myscout.imageRepository

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import be.helmo.myscout.MainActivity
import be.helmo.myscout.model.Phase
import be.helmo.myscout.repositories.IImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.take
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList


class ImageRepository : IImageRepository{

    override fun createDirectoryAndSaveImage(imageToSave: Bitmap, directoryName: String) : Uri {
        val directory = File(MainActivity.appContext.getExternalFilesDir(null), String.format("%s%s", "/images/", directoryName))
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory.absolutePath, String.format("%s.jpeg", directory.listFiles()?.size))
        try {
            val out = FileOutputStream(file,true)
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            Toast.makeText(MainActivity.appContext, "Erreur lors de l'enregistrement de la photo", Toast.LENGTH_LONG).show()
        }

        return file.toUri()
    }

    override fun getImages(imagesDirectory: String): ArrayList<Uri> {
        val images: ArrayList<Uri> = ArrayList()
        val dir = File(MainActivity.appContext.getExternalFilesDir(null), String.format("%s%s", "/images/", imagesDirectory))
        val files = dir.listFiles()
        files?.forEach {
            images.add(it.toUri())
        }

        return images
    }

    override fun deleteImage(imageToDelete: Uri?){
        val file = File(imageToDelete?.path!!)
        file.delete()
    }

    override fun deletePhaseImages(phase: Phase) {
        val dir = File(MainActivity.appContext.getExternalFilesDir(null), String.format("%s%s", "/images/", phase.id.toString()))
        val files = dir.listFiles()
        files?.forEach {
            it.delete()
        }
        dir.delete()
    }

    override fun deletePhasesImages(phases: List<Phase?>?) {
        phases?.forEach {
            deletePhaseImages(it!!)
        }
    }

}