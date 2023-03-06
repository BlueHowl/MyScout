package be.helmo.myscout.view.meeting.phaseeditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import be.helmo.myscout.MainActivity
import be.helmo.myscout.R
import be.helmo.myscout.presenters.PhaseFragmentInterface
import be.helmo.myscout.presenters.PhasePresenter
import java.io.ByteArrayOutputStream
import java.util.*


class EditPhaseFragment : Fragment(), PhaseFragmentInterface {
    lateinit var presenter: PhasePresenter

    private var images: ArrayList<Uri?>? = null

    private var position = 0

    private val pickImageCode = 0
    private var duringText: EditText? = null
    private var resumeText: EditText? = null
    private var phasePrevPhotosBtn: Button? = null
    private var phaseNextPhotosBtn: Button? = null
    private var phaseAddPhotosBtn: Button? = null
    private var phaseAbortBtn: Button? = null
    private var phaseValidateBtn: Button? = null
    private var imageSwitcher: ImageSwitcher? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_phase, container, false)

        // link des composants
        images = ArrayList()
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
            activity?.onBackPressed() //todo changer?
        }

        phaseValidateBtn?.setOnClickListener() {
            if(duringText?.text.toString().isEmpty() || resumeText?.text.toString().isEmpty())
                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            else{
                presenter?.addPhase(
                    duringText?.text.toString(),
                    resumeText?.text.toString(),
                    images.toString()
                )
                activity?.onBackPressed() //todo changer?
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("PhaseFragment", "onViewCreated called")

        val menuTitle = requireActivity().findViewById<TextView>(R.id.menu_title)
        menuTitle.text = getString(R.string.app_phase_edit_title)
    }

    private fun pickImagesIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Choisir photo"), pickImageCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == pickImageCode) {
            if(resultCode == Activity.RESULT_OK){
                if(data!!.clipData != null){
                    val count = data.clipData!!.itemCount
                    for(i in 0 until count){
                        val imageUri = compressUri(context, data.clipData!!.getItemAt(i).uri)
                        images?.add(imageUri)
                    }
                    imageSwitcher?.setImageURI(images?.get(0))
                    position = 0
                } else if(data.data != null){
                    val imageUri = compressUri(context, data.data)
                    images?.add(imageUri)
                    imageSwitcher?.setImageURI(imageUri)
                    position = 0
                }
            }
        }
    }

    private fun getImageUri(inContext: Context?, inImage: Bitmap?): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext?.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun compressUri(c: Context?, uri: Uri?): Uri? {
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        BitmapFactory.decodeStream(c?.contentResolver?.openInputStream(uri!!), null, o)
        val o2 = BitmapFactory.Options()
        o2.inSampleSize = 2
        return getImageUri(c,BitmapFactory.decodeStream(uri?.let { c?.contentResolver?.openInputStream(it) }, null, o2))
    }

    companion object {
        //private const val TAG = "PhaseFragment"
        fun newInstance(mainActivity: MainActivity): EditPhaseFragment {
            val phasePresenter = PhasePresenter()
            val fragment = EditPhaseFragment()
            fragment.setPresenter(phasePresenter)
            return fragment
        }
    }

    override fun setPresenter(presenter: PhasePresenter) {
        this.presenter=presenter
    }
}