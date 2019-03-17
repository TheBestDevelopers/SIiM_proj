package com.thebestdevelopers.exifphotogallery.fragments.gallery

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thebestdevelopers.exifphotogallery.R
import kotlinx.android.synthetic.main.fragment_gallery.*

class GalleryFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private var recyclerViewAdapter: GalleryRecyclerViewAdapter? = null
    private var photosList: ArrayList<PhotoFile> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        checkPermissions()
        return inflater.inflate(R.layout.fragment_gallery, container, false)
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
            var photo = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            var file = PhotoFile(photo)
            file.extractExifData()
            photosList.add(file)
        }
        cursor.close()
    }

    private fun setupRecyclerAdapter() {
        recyclerViewAdapter = GalleryRecyclerViewAdapter(photosList)
        val layout = GridLayoutManager(requireContext(), 4)
        rv_photos.layoutManager = layout
        rv_photos.adapter = recyclerViewAdapter
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
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
