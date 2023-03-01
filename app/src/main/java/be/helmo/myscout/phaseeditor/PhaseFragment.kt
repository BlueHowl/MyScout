package be.helmo.myscout.phaseeditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import be.helmo.myscout.R
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.*


class PhaseFragment : Fragment() {
    private var images: ArrayList<Uri?>? = null

    private var position = 0;

    private val PICK_IMAGES_CODE = 0
    var timePicker: TimePicker? = null
    var duringText: EditText? = null
    var resumeText: EditText? = null
    var phasePrevPhotosBtn: Button? = null
    var phaseNextPhotosBtn: Button? = null
    var phaseAddPhotosBtn: Button? = null
    var phaseAbortBtn: Button? = null
    var phaseValidateBtn: Button? = null
    var imageSwitcher: ImageSwitcher? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.phase_fragment, container, false)

        // link des composants
        images = ArrayList()
        timePicker = view.findViewById(R.id.phase_start_time)
        duringText = view.findViewById(R.id.phase_during)
        resumeText = view.findViewById(R.id.phase_resume)
        phasePrevPhotosBtn = view.findViewById(R.id.phase_previous_photo_btn)
        phaseNextPhotosBtn = view.findViewById(R.id.phase_next_photo_btn)
        phaseAddPhotosBtn = view.findViewById(R.id.phase_pick_photos_btn)
        phaseAbortBtn = view.findViewById(R.id.phase_abort_btn)
        phaseValidateBtn = view.findViewById(R.id.phase_validate_btn)
        imageSwitcher = view.findViewById(R.id.phase_image_switcher)

        // initialisation des composants
        imageSwitcher?.setFactory { ImageView(context) }

        // assignation des listeners
        phasePrevPhotosBtn?.setOnClickListener() {
            if(position > 0) {
                position--
                imageSwitcher?.setImageURI(images?.get(position))
            }else{
                Toast.makeText(context, "Début de la liste", Toast.LENGTH_SHORT).show()
            }
        }

        phaseNextPhotosBtn?.setOnClickListener() {
            if(position < images?.size!! - 1) {
                position++
                imageSwitcher?.setImageURI(images?.get(position))
            }else{
                Toast.makeText(context, "Fin de la liste", Toast.LENGTH_SHORT).show()
            }
        }

        phaseAddPhotosBtn?.setOnClickListener() {
            pickImagesIntent()
        }

        phaseAbortBtn?.setOnClickListener() {
            // TODO
        }

        phaseValidateBtn?.setOnClickListener() {
            // TODO
        }
        return view
    }

    fun pickImagesIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Choisir photo"), PICK_IMAGES_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_IMAGES_CODE) {
            if(resultCode == Activity.RESULT_OK){
                if(data!!.clipData != null){
                    val count = data.clipData!!.itemCount
                    for(i in 0 until count){
                        val imageUri = compressUri(context, data.clipData!!.getItemAt(i).uri)
                        images?.add(imageUri)
                    }
                    imageSwitcher?.setImageURI(images?.get(0))
                    position = 0;
                } else if(data.data != null){
                    val imageUri = compressUri(context, data.data)
                    images?.add(imageUri)
                    imageSwitcher?.setImageURI(imageUri)
                    position = 0;
                }
            }
        }
    }

    fun getImageUri(inContext: Context?, inImage: Bitmap?): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext?.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    fun compressUri(c: Context?, uri: Uri?): Uri? {
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        BitmapFactory.decodeStream(c?.contentResolver?.openInputStream(uri!!), null, o)
        val o2 = BitmapFactory.Options()
        o2.inSampleSize = 2
        return getImageUri(c,BitmapFactory.decodeStream(uri?.let { c?.contentResolver?.openInputStream(it) }, null, o2))
    }

    companion object {
        private const val TAG = "PhaseFragment"
        fun newInstance(): PhaseFragment {
            return PhaseFragment()
        }
    }
}