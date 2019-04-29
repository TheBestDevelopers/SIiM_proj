package com.thebestdevelopers.exifphotogallery

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import com.thebestdevelopers.exifphotogallery.fragments.gallery.GalleryFragment
import com.thebestdevelopers.exifphotogallery.fragments.gallery.PhotoFile
import com.thebestdevelopers.exifphotogallery.fragments.photodetails.DetailsFragment
import com.thebestdevelopers.exifphotogallery.fragments.search.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), GalleryFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener {

    private var currentFragment = R.id.navigation_gallery
    private val navigation_details = Int.MAX_VALUE
    private val REQUEST_IMAGE_CAPTURE = 1
    var currentPhotoPath: String = ""

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_gallery -> {
                //viewText.setText(getString(R.string.gallery_tag))
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.mainFrame, GalleryFragment.newInstance(), getString(R.string.gallery_tag))
                    .addToBackStack(null)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                //viewText.setText(getString(R.string.search_tag))
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
        //viewText.setText(getString(R.string.gallery_tag))
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.mainFrame, GalleryFragment.newInstance(), getString(R.string.gallery_tag))
            .addToBackStack(null)
            .commit()
    }

    private fun startCamera() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        launchCamera()
                    }
                }
            }).check()

    }

    private fun launchCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
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
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            val file = File(Environment.getExternalStorageDirectory().path, "photo.jpg")
            val uri = Uri.fromFile(file)
            galleryAddPic()
            onFragmentInteraction(PhotoFile(uri.path))
        }
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
            currentPhotoPath = absolutePath
        }
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }

    override fun onBackPressed() {
        val bottomNavView = this.findViewById<BottomNavigationView>(R.id.navigation)
        val selectedItem = bottomNavView.selectedItemId
        if (R.id.navigation_gallery != selectedItem)
            setHomeItem()
        else {
            finish()
            System.exit(0)
        }
    }

    private fun setHomeItem() {
        val bottomNavView = this.findViewById<BottomNavigationView>(R.id.navigation)
        bottomNavView.selectedItemId = R.id.navigation_gallery
        //viewText.text = getString(R.string.gallery_tag)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrame, GalleryFragment.newInstance(), getString(R.string.gallery_tag))
            .addToBackStack(null)
            .commit()
        currentFragment = R.id.navigation_gallery
    }

    override fun onFragmentInteraction(photo: PhotoFile) {
        //viewText.text = getString(R.string.details_tag)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrame, DetailsFragment.newInstance(photo))
            .addToBackStack(null)
            .commitAllowingStateLoss()
        currentFragment = navigation_details
    }
}
