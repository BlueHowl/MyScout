package be.helmo.myscout.view.phases

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.*
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.*
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import be.helmo.myscout.BuildConfig
import be.helmo.myscout.R
import be.helmo.myscout.factory.PresenterSingletonFactory
import be.helmo.myscout.presenters.interfaces.IEditPhaseFragment
import be.helmo.myscout.presenters.viewmodel.PhaseViewModel
import be.helmo.myscout.view.interfaces.IPhasePresenter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EditPhaseFragment : Fragment(), IEditPhaseFragment {
    lateinit var phasePresenter: IPhasePresenter
    var phase: PhaseViewModel? = null
    var editMode: Boolean = false

    var images: ArrayList<Uri?>? = ArrayList()

    var position = 0

    val pickImageCode = 0

    private val cameraPhotoCode = 1

    var photoFile: File? = null
    lateinit var nameText: EditText
    lateinit var duringText: EditText
    lateinit var resumeText: EditText
    lateinit var phasePrevPhotosBtn: Button
    lateinit var phaseNextPhotosBtn: Button
    lateinit var phaseAddPhotosBtn: Button
    lateinit var phaseAbortBtn: Button
    lateinit var phaseValidateBtn: Button
    lateinit var imageSwitcher: ImageSwitcher
    lateinit var favorite: RatingBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phasePresenter = PresenterSingletonFactory.instance!!.getPhasePresenter()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_phase, container, false)

        // link des composants
        nameText = view.findViewById(R.id.et_phase_name)
        duringText = view.findViewById(R.id.phase_duration)
        resumeText = view.findViewById(R.id.phase_resume)
        phasePrevPhotosBtn = view.findViewById(R.id.phase_previous_photo_btn)
        phaseNextPhotosBtn = view.findViewById(R.id.phase_next_photo_btn)
        phaseAddPhotosBtn = view.findViewById(R.id.phase_pick_photos_btn)

        val showPopupMenu = PopupMenu(
            context,
            phaseAddPhotosBtn
        )

        showPopupMenu.menu.add(Menu.NONE, 0, 0, "Depuis la galerie")
        showPopupMenu.menu.add(Menu.NONE, 1, 1, "Depuis la caméra")

        showPopupMenu.setOnMenuItemClickListener { menuItem ->
            val  id = menuItem.itemId
            if (id==0){
                pickImagesIntent()
            }
            else if(id==1){
                dispatchTakePictureIntent()
            }
            false
        }
        phaseAddPhotosBtn.setOnClickListener {
            showPopupMenu.show()
        }

        phaseAbortBtn = view.findViewById(R.id.phase_abort_btn)
        phaseValidateBtn = view.findViewById(R.id.phase_validate_btn)
        imageSwitcher = view.findViewById(R.id.phase_image_switcher)
        favorite = view.findViewById(R.id.favorite)


        // initialisation des composants
        imageSwitcher.setFactory { ImageView(context) }

        // assignation des listeners
        phasePrevPhotosBtn.setOnClickListener() {
            previousImage()
        }

        phaseNextPhotosBtn.setOnClickListener() {
            nextImage()
        }

        phaseAbortBtn.setOnClickListener() {
            position = 0
            if(images?.size!! > 0) {
                imageSwitcher.setImageURI(images?.get(position) ?: Uri.EMPTY)
            }
            activity?.onBackPressed()
        }

        favorite.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                favorite.rating = when(favorite.rating) {
                    1F -> 0F
                    0F -> 1F
                    else -> {0F}
                }
            }
            true
        })

        phaseValidateBtn.setOnClickListener() {
            if(duringText.text.toString().isEmpty() || resumeText.text.toString().isEmpty())
                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            else{
                if(editMode){
                    phasePresenter.modifyPhase(phase!!.phaseId!!,
                        nameText.text.toString(),
                        resumeText.text.toString(),
                        duringText.text.toString().toLong(),
                        favorite.rating >= 1F)
                }else{
                    phasePresenter.addPhase(
                        phase!!.phaseId!!,
                        nameText.text.toString(),
                        duringText.text.toString().toLong(),
                        resumeText.text.toString(),
                        favorite.rating >= 1F
                    )

                }
                activity?.onBackPressed() //todo changer?
            }
        }

        val gestureDetector = GestureDetector(activity, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val SWIPE_THRESHOLD = 50
                val SWIPE_VELOCITY_THRESHOLD = 100
                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y
                if (Math.abs(diffX) > Math.abs(diffY) &&
                    Math.abs(diffX) > SWIPE_THRESHOLD &&
                    Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD
                ) {
                    if (diffX > 0) {
                        previousImage()
                    } else {
                        nextImage()
                    }
                    return true
                }
                return false
            }

            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)
                if (images?.size!! > 0) {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Supprimer l'image ?")
                    builder.setPositiveButton("Oui") { _, _ ->
                        phasePresenter.deleteImage(images?.get(position))
                        images?.removeAt(position)
                        if (images?.size!! > 0) {
                            previousImage()
                            imageSwitcher.setImageURI(images?.get(position))
                        } else {
                            imageSwitcher.setImageURI(Uri.EMPTY)
                        }
                    }
                    builder.setNegativeButton("Non") { _, _ -> }
                    builder.show()
                }
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                val dialog = Dialog(context!!)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val imageView = ImageView(context)
                imageView.setImageURI(getImageUri(context!!, rotateImageIfRequired(images?.get(position))))
                dialog.addContentView(
                    imageView, RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                if(dialog.isShowing){
                    dialog.dismiss()
                }else{
                    dialog.show()
                }
                return true
            }
        })

        imageSwitcher?.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event)
            true}

        applyPhaseValues()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("PhaseFragment", "onViewCreated called")

        //change le titre du menu
        val menuTitle = requireActivity().findViewById<TextView>(R.id.menu_title)
        menuTitle.text = getString(R.string.app_phase_edit_title)

        //rend invisible le btn add_element
        val addElement = requireActivity().findViewById<ImageView>(R.id.add_element)
        addElement.visibility = View.GONE

        //rend le btn edit_element invisible
        val editElement = requireActivity().findViewById<ImageView>(R.id.edit_element)
        editElement.visibility = View.GONE

        //rend le btn delete_element visible
        if(editMode) {
            val deleteElement = requireActivity().findViewById<ImageView>(R.id.delete_element)
            deleteElement.visibility = View.VISIBLE
        }
    }

    override fun setPhaseValues(phase: PhaseViewModel, images: ArrayList<Uri>?) {
        this.phase = phase
        if(images != null) {
            editMode = true
            this.images?.addAll(images)
        }
    }

    fun applyPhaseValues(){
        nameText.setText(phase?.name)
        duringText.setText(phase?.duration.toString())
        resumeText.setText(phase?.description)
        favorite.rating = if(phase?.favorite == true) 1F else 0F

        images?.forEach {
            if(it != null)
                imageSwitcher.setImageURI(it)
        }

        if(images?.size!! > 0){
            position = 0
            imageSwitcher.setImageURI(images?.get(position))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == pickImageCode) {
            if(resultCode == Activity.RESULT_OK){
                if(data!!.clipData != null){
                    val count = data.clipData!!.itemCount
                    for(i in 0 until count){
                        images?.add(compressUri(requireContext(),data.clipData!!.getItemAt(i).uri))
                    }
                    position = images?.size!! - 1
                    imageSwitcher.setImageURI(images?.get(position))
                } else if(data.data != null){
                    images?.add(compressUri(requireContext(),data.data!!))
                    position = images?.size!! - 1
                    imageSwitcher.setImageURI(images?.get(position))
                }
                data.data = null
                data.clipData = null
            }
        }else if(requestCode == cameraPhotoCode){
            if(resultCode == Activity.RESULT_OK){
                if(photoFile != null){
                    photoFile?.let {
                        val imageBitmap = rotateImageIfRequired(it.toUri())
                        val imageUri = getImageUri(requireContext(),imageBitmap)
                        images?.add(compressUri(requireContext(),imageUri))
                        position = images?.size!! - 1
                        imageSwitcher.setImageURI(images?.get(position))
                    }
                }
            }
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun pickImagesIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, pickImageCode)
    }

    fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                photoFile = try {
                    File.createTempFile(
                        "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}_",
                        ".jpg",
                        requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    // print an error msg
                    Log.d("ERROR", "An error occured")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        Objects.requireNonNull(requireContext()),
                        BuildConfig.APPLICATION_ID + ".provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, cameraPhotoCode)
                }
            }
        }
    }

    fun previousImage() {
        if(position > 0) {
            imageSwitcher.setInAnimation(context, R.anim.from_left)
            imageSwitcher.setOutAnimation(context, R.anim.to_right)

            position--
            imageSwitcher.setImageURI(images?.get(position))
        }
    }

    fun nextImage() {
        if(position < images?.size!! - 1) {
            imageSwitcher.setInAnimation(context, R.anim.from_right)
            imageSwitcher.setOutAnimation(context, R.anim.to_left)

            position++
            imageSwitcher.setImageURI(images?.get(position))
        }
    }

    // fonctions pour les images
    @Throws(IOException::class)
    private fun rotateImageIfRequired(selectedImage: Uri?): Bitmap {
        //get the path of the image from the context
        val ei = ExifInterface(selectedImage?.path!!)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val imageBitmap = selectedImage.path?.let { BitmapFactory.decodeFile(it) }
        val bitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(imageBitmap, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(imageBitmap, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(imageBitmap, 270F)
            else -> imageBitmap!!
        }
        return bitmap
    }

    fun rotateImage(source: Bitmap?, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source!!, 0, 0, source.width, source.height, matrix, true)
    }

    fun getImageUri(inImage: Bitmap?, needCompress: Boolean): Uri {
        val bytes = ByteArrayOutputStream()
        if(needCompress){
            inImage?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        }
        return phasePresenter.saveImage(inImage!!, phase!!.phaseId!!)
    }

    fun compressUri(c: Context?, uri: Uri?): Uri {
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        BitmapFactory.decodeStream(c?.contentResolver?.openInputStream(uri!!), null, o)
        val o2 = BitmapFactory.Options()
        o2.inSampleSize = 2
        return getImageUri(BitmapFactory.decodeStream(c?.contentResolver?.openInputStream(uri!!), null, o2), true)

    }

    companion object {
        const val TAG = "EditPhaseFragment"
        fun newInstance(): EditPhaseFragment {
            return EditPhaseFragment()
        }
    }

}
