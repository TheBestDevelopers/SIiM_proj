package com.thebestdevelopers.exifphotogallery.fragments.search

import android.app.DatePickerDialog
import android.content.Context
import android.support.media.ExifInterface
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.thebestdevelopers.exifphotogallery.R
import com.thebestdevelopers.exifphotogallery.fragments.gallery.PhotoFile
import java.text.ParseException

import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors


class SearchFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private var exifSpinner: Spinner? = null
    private var allPhotosList: ArrayList<PhotoFile> = ArrayList()
    private var mRv_photos: RecyclerView? = null
    private var exifValue: EditText? = null
    private var exifValueSpinner: Spinner? = null
    private var okButton: Button? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)
        bindViews(rootView)
        setListeners()
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, createExifParametersList())
        exifSpinner?.adapter = adapter
        return rootView
    }

    private fun onExifValueClick() {
        if (exifSpinnerSelectedItemTag()?.equals(ExifInterface.TAG_DATETIME) == true) {

            val calendar = Calendar.getInstance()
            val datePickerOnDataSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val formatter = SimpleDateFormat(getString(R.string.default_date_pattern), Locale.US)
                exifValue?.setText(formatter.format(calendar?.time))
                exifValue?.setSelection(formatter.toPattern().length)
            }

            DatePickerDialog(
                requireContext(),
                datePickerOnDataSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun createExifParametersList(): ArrayList<ExifParameter> {
        val exifParameters = ArrayList<ExifParameter>()
        var item = ExifParameter("Date", ExifInterface.TAG_DATETIME)
        exifParameters.add(item)
        item = ExifParameter("Orientation", ExifInterface.TAG_ORIENTATION)
        exifParameters.add(item)
        item = ExifParameter("ISO speed", ExifInterface.TAG_ISO_SPEED_RATINGS)
        exifParameters.add(item)
        return exifParameters
    }

    override fun onStart() {
        super.onStart()
        getGalleryFromStorage()
        setupRecyclerAdapter()
    }

    override fun onStop() {
        super.onStop()
        allPhotosList.clear()
        exifValue?.text?.clear()
    }

    private fun onOkClick() {
        when (exifSpinnerSelectedItemTag()) {
            ExifInterface.TAG_DATETIME -> filterByDate(ExifInterface.TAG_DATETIME)
            ExifInterface.TAG_ORIENTATION -> filterByOrientation(ExifInterface.TAG_ORIENTATION)
            ExifInterface.TAG_ISO_SPEED_RATINGS -> filterByIsoSpeed(ExifInterface.TAG_ISO_SPEED_RATINGS)
        }
    }

    private fun filterByIsoSpeed(exifParameterTagName: String) {
        val userIsoSpeedValue = exifValueSpinner?.selectedItem as Int
        val filteredPhotoList =
            allPhotosList.stream().filter { v -> v.readSingleExifInt(exifParameterTagName, -100) == userIsoSpeedValue }
                .collect(Collectors.toList())
        mRv_photos?.adapter = SearchRecycleViewAdapter(ArrayList(filteredPhotoList), listener)
        filteredPhotoList.clear()
    }

    private fun exifSpinnerSelectedItemTag(): String? {
        val exifParameter = exifSpinner?.selectedItem as ExifParameter?
        return exifParameter?.tagName
    }

    private fun filterByDate(exifParameterTagName: String) {
        val userDate = exifValue?.text
        val formatter = SimpleDateFormat(getString(R.string.colon_date_pattern), Locale.US)
        val formatter2 = SimpleDateFormat(getString(R.string.default_date_pattern), Locale.US)
        try {
            val filteredPhotoList = allPhotosList.stream().filter { v ->
                v.readSingleExifString(exifParameterTagName) != null
                        && formatter.parse(v.readSingleExifString(exifParameterTagName)?.substring(0..10)) ==
                        formatter2.parse(userDate?.toString())
            }.collect(Collectors.toList())

            mRv_photos?.adapter = SearchRecycleViewAdapter(ArrayList(filteredPhotoList), listener)
            filteredPhotoList.clear()
        } catch (e: ParseException) {
            Toast.makeText(requireContext(), "Enter date!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun filterByOrientation(exifParameterTagName: String) {
        val userRotation = (exifValueSpinner?.selectedItem as ExifOrientationValue).value
        val filteredPhotoList = allPhotosList.stream().filter { v ->
            convertRotationValueToDegrees(
                v.readSingleExifInt(
                    exifParameterTagName,
                    ExifInterface.ORIENTATION_NORMAL
                )
            ) == userRotation
        }.collect(Collectors.toList())
        mRv_photos?.adapter = SearchRecycleViewAdapter(ArrayList(filteredPhotoList), listener)
        filteredPhotoList.clear()
    }

    private fun convertRotationValueToDegrees(orientation: Int?): Int {
        var rotation = 0

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotation = 90
            ExifInterface.ORIENTATION_ROTATE_180 -> rotation = 180
            ExifInterface.ORIENTATION_ROTATE_270 -> rotation = 270
        }

        return rotation
    }

    private fun getGalleryFromStorage() {
        val cursor =
            requireContext().contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
        while (cursor != null && cursor.moveToNext()) {
            val photo = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            val file = PhotoFile(photo)
            allPhotosList.add(file)
        }
        cursor.close()
    }

    private fun setupRecyclerAdapter() {
        mRv_photos?.layoutManager = GridLayoutManager(requireContext(), 4)
        mRv_photos?.adapter = SearchRecycleViewAdapter(allPhotosList, listener)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun bindViews(rootView: View) {
        exifSpinner = rootView.findViewById(R.id.exif_spinner)
        mRv_photos = rootView.findViewById(R.id.rv_filtered_photos)
        exifValue = rootView.findViewById(R.id.exif_value)
        exifValueSpinner = rootView.findViewById(R.id.exif_value_spinner)
        okButton = rootView.findViewById(R.id.button_ok)
    }

    private fun setListeners() {
        okButton?.setOnClickListener { onOkClick() }
        exifValue?.setOnClickListener { onExifValueClick() }
        exifSpinner?.onItemSelectedListener = ExifSpinnerOnItemSelectedListener(
            requireContext(),
            exifValue,
            exifValueSpinner,
            mRv_photos,
            SearchRecycleViewAdapter(allPhotosList, listener),
            allPhotosList
        )
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(photo: PhotoFile)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
