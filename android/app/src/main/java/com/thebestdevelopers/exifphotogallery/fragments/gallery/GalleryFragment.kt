package com.thebestdevelopers.exifphotogallery.fragments.gallery

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thebestdevelopers.exifphotogallery.R

class GalleryFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private var photosList: ArrayList<PhotoFile> = ArrayList()
    private var mRv_photos: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_gallery, container, false)
        mRv_photos = rootView.findViewById(R.id.rv_photos) as RecyclerView
        checkPermissions()
        return rootView
    }

    private fun checkPermissions() {
        val permission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        else {
            getGalleryFromStorage()
            setupRecyclerAdapter()
        }
    }

    private fun getGalleryFromStorage() {
        val cursor =
            requireContext().contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
        while (cursor != null && cursor.moveToNext()) {
            val photo = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            val file = PhotoFile(photo)
            file.extractExifData()
            photosList.add(file)
        }
        cursor.close()
    }

    private fun setupRecyclerAdapter() {
        mRv_photos?.layoutManager = GridLayoutManager(requireContext(), 4)
        mRv_photos?.adapter = GalleryRecyclerViewAdapter(photosList, listener)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if ((grantResults[0] and grantResults.size) == PackageManager.PERMISSION_GRANTED) {
                getGalleryFromStorage()
                setupRecyclerAdapter()
            }
        }
    }
}
