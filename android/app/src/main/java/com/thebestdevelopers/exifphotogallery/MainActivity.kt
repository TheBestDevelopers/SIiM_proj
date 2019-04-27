package com.thebestdevelopers.exifphotogallery

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.thebestdevelopers.exifphotogallery.fragments.gallery.GalleryFragment
import com.thebestdevelopers.exifphotogallery.fragments.gallery.PhotoFile
import com.thebestdevelopers.exifphotogallery.fragments.photodetails.DetailsFragment
import com.thebestdevelopers.exifphotogallery.fragments.search.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource


class MainActivity : AppCompatActivity(), GalleryFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener {

    private var mCurrentPhotoPath: String? = null
    val REQUEST_IMAGE_CAPTURE = 101
    var easyImage : EasyImage? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_gallery -> {
                viewText.setText(getString(R.string.gallery_tag))
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.mainFrame, GalleryFragment.newInstance(), getString(R.string.gallery_tag))
                    .addToBackStack(null)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                viewText.setText(getString(R.string.search_tag))
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.mainFrame, SearchFragment.newInstance(), getString(R.string.search_tag))
                    .addToBackStack(null)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_camera -> {
                startCamera()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewText.setText(getString(R.string.gallery_tag))
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.mainFrame, GalleryFragment.newInstance(), getString(R.string.gallery_tag))
            .addToBackStack(null)
            .commit()
    }

    private fun startCamera() {
        /*Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }*/
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted())
                        launchCamera()
                }
            }).check()

    }

    private fun launchCamera() {
        easyImage = EasyImage.Builder(this)
            .setCopyImagesToPublicGalleryFolder(true)
            .build()
        easyImage?.openCameraForImage(this)
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(mCurrentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /*if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            galleryAddPic()
            onFragmentInteraction(PhotoFile(mCurrentPhotoPath!!))
        }*/
        easyImage?.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                onPhotosReturned(imageFiles)
            }

            override fun onImagePickerError(error: Throwable, source: MediaSource) {
                //Some error handling
                error.printStackTrace()
            }

            override fun onCanceled(source: MediaSource) {
                //Not necessary to remove any files manually anymore
            }
        })
    }

    private fun onPhotosReturned(data : Array<MediaFile>) {

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }

    override fun onFragmentInteraction(photo: PhotoFile) {
        viewText.text = getString(R.string.details_tag)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrame, DetailsFragment.newInstance(photo))
            .addToBackStack(null)
            .commit()
    }
}
