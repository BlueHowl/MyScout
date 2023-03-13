package be.helmo.myscout.view.phases

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import be.helmo.myscout.R
import be.helmo.myscout.factory.PresenterSingletonFactory
import be.helmo.myscout.presenters.interfaces.IEditPhaseFragment
import be.helmo.myscout.presenters.viewmodel.PhaseViewModel
import be.helmo.myscout.view.interfaces.IPhasePresenter
import java.io.ByteArrayOutputStream


class EditPhaseFragment : Fragment(), IEditPhaseFragment {
    lateinit var phasePresenter: IPhasePresenter

    var phase: PhaseViewModel? = null
    var editMode: Boolean = false

    var images: ArrayList<Uri?>? = ArrayList()

    var position = 0

    val pickImageCode = 0

    val permission_request_code = 200
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 7
    //test
    val cameraPhotoCode = 1

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
        onRequestPermissionsResult(
            permission_request_code,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            intArrayOf(PackageManager.PERMISSION_GRANTED)
        )
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
                cameraIntent()
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
            activity?.onBackPressed() //todo changer?
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
        })

        imageSwitcher?.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event)
            true}

        applyPhaseValues()

        return view
    }

    private fun cameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            permission_request_code
        )
        if(intent.resolveActivity(requireActivity().packageManager) != null){
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                startActivityForResult(intent, cameraPhotoCode)
            }
        }
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
    }

    fun pickImagesIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Choisir photo"), pickImageCode)
    }

    private fun checkAndRequestPermissions(): Boolean {
        val camera = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.CAMERA
        )
        val write = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val read =
            ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (write != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                listPermissionsNeeded.toTypedArray<String>(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        Log.d("in fragment on request", "Permission callback called-------")
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms: MutableMap<String, Int> = HashMap()
                // Initialize the map with both permissions
                perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                // Fill with actual results from user
                if (grantResults.size > 0) {
                    var i = 0
                    while (i < permissions.size) {
                        perms[permissions[i]] = grantResults[i]
                        i++
                    }
                    // Check for permission
                    if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(
                            "in fragment on request",
                            "CAMERA permission granted"
                        )
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(
                            "in fragment on request",
                            "Some permissions are not granted ask again "
                        )
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(), Manifest.permission.CAMERA
                            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        ) {
                            // the equivalent of showDialogOK from java
                            AlertDialog.Builder(requireActivity())
                                .setMessage("Les permissions pour la Camera et le Stockage sont requis pour cet application")
                                .setPositiveButton("OK") { _, _ ->
                                    checkAndRequestPermissions()
                                }
                                .setNegativeButton("Annuler", null)
                                .create()
                                .show()
                        } else {
                            Toast.makeText(
                                activity,
                                "Aller dans les parametres de l'application et autoriser les permissions",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
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
                    imageSwitcher.setImageURI(images?.get(0))
                    position = 0
                } else if(data.data != null){
                    val imageUri = compressUri(context, data.data)
                    images?.add(imageUri)
                    imageSwitcher.setImageURI(images?.get(0))
                    position = 0
                }
            }
        }else if(requestCode == cameraPhotoCode){
            if(resultCode == Activity.RESULT_OK){
                val imageBitmap = data?.extras?.get("data") as Bitmap
                val imageUri = getImageUri(imageBitmap)
                images?.add(imageUri)
                imageSwitcher.setImageURI(images?.get(0))
                position = 0
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

    fun getImageUri(inImage: Bitmap?): Uri {
        val bytes = ByteArrayOutputStream()
        inImage?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        return phasePresenter.saveImage(inImage!!, phase!!.phaseId!!)
    }

    fun compressUri(c: Context?, uri: Uri?): Uri {
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        BitmapFactory.decodeStream(c?.contentResolver?.openInputStream(uri!!), null, o)
        val o2 = BitmapFactory.Options()
        o2.inSampleSize = 2
        return getImageUri(BitmapFactory.decodeStream(uri?.let { c?.contentResolver?.openInputStream(it) }, null, o2))
    }

    companion object {
        const val TAG = "EditPhaseFragment"
        fun newInstance(): EditPhaseFragment {
            return EditPhaseFragment()
        }
    }

}