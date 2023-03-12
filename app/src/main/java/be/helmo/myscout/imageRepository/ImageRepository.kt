package be.helmo.myscout.imageRepository

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import be.helmo.myscout.MainActivity
import be.helmo.myscout.repositories.IImageRepository
import java.io.File
import java.io.FileOutputStream
import java.util.ArrayList


class ImageRepository : IImageRepository{

    override fun createDirectoryAndSaveImage(imageToSave: Bitmap, directoryName: String) : Uri {
        val directory = File(MainActivity.appContext.getExternalFilesDir(null), String.format("%s%s", "/images/", directoryName))
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory.absolutePath, String.format("%d.jpeg", directory.listFiles()?.size))
        try {
            val out = FileOutputStream(file)
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

}