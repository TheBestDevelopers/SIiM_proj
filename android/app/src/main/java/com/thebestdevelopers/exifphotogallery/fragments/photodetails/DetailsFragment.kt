package com.thebestdevelopers.exifphotogallery.fragments.photodetails

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.media.ExifInterface
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.thebestdevelopers.exifphotogallery.R
import com.thebestdevelopers.exifphotogallery.fragments.gallery.PhotoFile
import java.io.File
import java.io.IOException
import kotlin.math.roundToInt

class DetailsFragment : Fragment() {
    private lateinit var mPhoto: PhotoFile
    private lateinit var mImageView: ImageView
    private lateinit var mAdapter : DetailsRecyclerViewAdapter
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var linearLayoutManager : LinearLayoutManager
    private lateinit var exifInterface : ExifInterface
    private lateinit var searchView: SearchView
    private lateinit var mScrollView: ScrollView

    private val lastVisibleItemPosition: Int
        get() = linearLayoutManager.findLastVisibleItemPosition()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = mPhoto.mPath.subSequence(mPhoto.mPath.lastIndexOf("/")+1, mPhoto.mPath.lastIndexOf("."))
        mScrollView = inflater.inflate(R.layout.fragment_details, container, false) as ScrollView

        mImageView = mScrollView.findViewById(R.id.mImageViewDetails)
        Picasso.get().load(File(mPhoto.mPath)).into(mImageView)

        val recyclerView = mScrollView.findViewById<RecyclerView>(R.id.mDetailsList)
        recyclerView?.layoutManager = LinearLayoutManager(mScrollView.context)
        DetailContent.clear()
        DetailsSettingsContent.clear().fill()
        exifInterface = ExifInterface(mPhoto.mPath)
        DetailContent.addItems(func = {s -> exifInterface.getAttribute(s)?:""})
        mAdapter = DetailsRecyclerViewAdapter(DetailContent.ITEMS)
        recyclerView?.adapter = mAdapter
        linearLayoutManager = recyclerView?.layoutManager as LinearLayoutManager

        return mScrollView
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.details, menu)
        //search items
        searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                //filter recycler view when query submitted
                mAdapter.filter.filter(p0)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                //filter recycler view when query changed
                mAdapter.filter.filter(p0)
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPause() {
        super.onPause()
        mAdapter.saveValues()
        saveAttributesAndShowToast()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveAttributesAndShowToast()
    }

    private fun saveAttributesAndShowToast(){
        val dirtyList = DetailContent.getDirtyList()
        if(dirtyList.isNotEmpty())
        try {
            dirtyList.map { detailItem -> exifInterface.setAttribute(detailItem.parameter.tag, if(detailItem.value == "") null else detailItem.value)}
            exifInterface.saveAttributes()
            Toast.makeText(activity, "Saved: "+dirtyList.size+" attributes.", Toast.LENGTH_LONG).show()
        }   catch (exception : IOException){
            Toast.makeText(activity, exception.message, Toast.LENGTH_LONG).show()
            //throw exception
        }
        Toast.makeText(activity, "Nothing to save", Toast.LENGTH_LONG).show()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(item: DetailContent.DetailItem)
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
