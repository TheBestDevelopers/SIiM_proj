package com.thebestdevelopers.exifphotogallery

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.thebestdevelopers.exifphotogallery.fragments.gallery.GalleryFragment
import com.thebestdevelopers.exifphotogallery.fragments.gallery.PhotoFile
import com.thebestdevelopers.exifphotogallery.fragments.photodetails.DetailsFragment
import com.thebestdevelopers.exifphotogallery.fragments.search.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), GalleryFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener {

    private var currentFragment = R.id.navigation_gallery
    private val navigation_details = Int.MIN_VALUE
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var imageUri : Uri


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_gallery -> {
                if (currentFragment != R.id.navigation_gallery) {
                    //viewText.setText(getString(R.string.gallery_tag))
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.mainFrame, GalleryFragment.newInstance(), getString(R.string.gallery_tag))
                        .addToBackStack(null)
                        .commit()
                    currentFragment = R.id.navigation_gallery
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                if (currentFragment != R.id.navigation_search) {
                    //viewText.setText(getString(R.string.search_tag))
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.mainFrame, SearchFragment.newInstance(), getString(R.string.search_tag))
                        .addToBackStack(null)
                        .commit()
                    R.id.navigation_search
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_camera -> {
                //startCamera()
                checkPermissionsForCamera()
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

    private fun checkPermissionsForCamera() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        startCamera()
                    }
                }
            }).check()
    }

    private fun startCamera() {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New image")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        var cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun getRealPathFromURI(uri: Uri): String {
        var path = ""
        if (contentResolver != null) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            onFragmentInteraction(PhotoFile(getRealPathFromURI(imageUri)))
        }
    }

    override fun onBackPressed() {
        val bottomNavView = this.findViewById<BottomNavigationView>(R.id.navigation)
        val selectedItem = bottomNavView.selectedItemId
        if (R.id.navigation_gallery != selectedItem || currentFragment == Int.MIN_VALUE)
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
        currentFragment = navigation_details
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrame, DetailsFragment.newInstance(photo))
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
}
