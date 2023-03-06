package be.helmo.myscout.imageRepository

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
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
            Log.e("imageRepo", "erreur enregistrement " + e.message)
            //todo toast ? e.printStackTrace()
        }

        return file.toUri()
    }

    override fun getImages(imagesDirectory: String): List<Bitmap> {
        val bitmaps: ArrayList<Bitmap> = ArrayList<Bitmap>()
        val dir = File(imagesDirectory)
        val files = dir.listFiles()
        files?.forEach {
            bitmaps.add(
                when {
                    Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                        MainActivity.appContext.contentResolver,
                        it.toUri()
                    )
                    else -> {
                        val source =
                            ImageDecoder.createSource(
                                MainActivity.appContext.contentResolver,
                                it.toUri()
                            )
                        ImageDecoder.decodeBitmap(source)
                    }
                }
            )
        }

        return bitmaps
    }

}