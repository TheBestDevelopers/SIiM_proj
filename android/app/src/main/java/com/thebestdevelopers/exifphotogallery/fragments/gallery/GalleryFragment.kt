package com.thebestdevelopers.exifphotogallery.fragments.gallery

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.thebestdevelopers.exifphotogallery.R

class GalleryFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private var photosList: ArrayList<PhotoFile> = ArrayList()
    private var mRv_photos: RecyclerView? = null
    private val columnNumb : Int = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_gallery, container, false)
        mRv_photos = rootView.findViewById(R.id.rv_photos) as RecyclerView
        checkPermissions()
        return rootView
    }

    private fun checkPermissions() {
        Dexter.withActivity(activity)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    //permission denied
                    Toast.makeText(context,"Do działania aplikacji wymagany jest dostęp do galerii zdjęć", Toast.LENGTH_LONG).show()
                }

                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    getGalleryFromStorage()
                    setupRecyclerAdapter()
                }
            }).check()
    }

    private fun getGalleryFromStorage() {
        val cursor =
            requireContext().contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
        while (cursor != null && cursor.moveToNext()) {
            var photo = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            var file = PhotoFile(photo)
            file.extractExifData()
            photosList.add(file)
        }
        cursor.close()
    }

    private fun setupRecyclerAdapter() {
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

        mRv_photos?.layoutManager = GridLayoutManager(requireContext(), columnNumb)
        mRv_photos?.adapter = GalleryRecyclerViewAdapter(photosList, listener, displayMetrics.widthPixels/columnNumb)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(photo: PhotoFile)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            GalleryFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
