package com.thebestdevelopers.exifphotogallery.fragments.photodetails

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso

import com.thebestdevelopers.exifphotogallery.R
import com.thebestdevelopers.exifphotogallery.fragments.gallery.PhotoFile
import kotlinx.android.synthetic.main.fragment_details.*
import java.io.File

class DetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mPhoto: PhotoFile? = null
    private var imageView: ImageView? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_details, container, false)
        imageView = view.findViewById<ImageView>(R.id.mImageViewDetails)
        Picasso.get().load(File(mPhoto?.mPath)).into(imageView)
        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    /*override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }*/

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
        fun newInstance(photo: PhotoFile) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    mPhoto = photo
                }
            }
    }
}
