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
import kotlin.collections.ArrayList
import kotlin.reflect.KClass


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
        activity?.title = getString(R.string.search_tag)
        bindViews(rootView)
        setListeners()
        val adapter =
            ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                SearchSettings.EXIF_PARAMETER_LIST
            )
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
        when (exifSpinnerSelectedItemValueType()) {
            Any::class -> filterBySpecialExifValues()
            String::class -> filterByStringExifValue(exifSpinnerSelectedItemTag())
            Int::class -> filterByIntExifValue(exifSpinnerSelectedItemTag())
        }
    }

    private fun filterBySpecialExifValues() {
        val itemTag = exifSpinnerSelectedItemTag()
        if (itemTag == ExifInterface.TAG_DATETIME)
            filterByDate(itemTag)
        else
            filterByPossibleValues(itemTag)
    }

    private fun filterByStringExifValue(exifParameterTagName: String?) {
        if (exifValueSpinner?.selectedItem != null && exifParameterTagName != null) {
            var userStringExifValue = exifValueSpinner?.selectedItem as String?

            if (userStringExifValue == getString(R.string.exif_null_value_desc))
                userStringExifValue = null

            val filteredPhotoList =
                allPhotosList.stream()
                    .filter { v -> v.readSingleExifString(exifParameterTagName) == userStringExifValue }
                    .collect(Collectors.toList())
            mRv_photos?.adapter = SearchRecycleViewAdapter(ArrayList(filteredPhotoList), listener)
            filteredPhotoList.clear()
        }
    }

    private fun filterByIntExifValue(exifParameterTagName: String?) {
        if (exifValueSpinner?.selectedItem != null && exifParameterTagName != null) {
            val userIntExifValue = exifValueSpinner?.selectedItem as IntegerExifValue

            val filteredPhotoList =
                allPhotosList.stream()
                    .filter { v -> v.readSingleExifInt(exifParameterTagName, Int.MIN_VALUE) == userIntExifValue.value }
                    .collect(Collectors.toList())
            mRv_photos?.adapter = SearchRecycleViewAdapter(ArrayList(filteredPhotoList), listener)
            filteredPhotoList.clear()
        }
    }

    private fun exifSpinnerSelectedItemValueType(): KClass<out Any>? {
        val exifParameter = exifSpinner?.selectedItem as ExifParameter?
        return exifParameter?.returnValueType
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

    private fun filterByPossibleValues(exifParameterTagName: String?) {
        if (exifParameterTagName != null) {
            val userIntExifValue = (exifValueSpinner?.selectedItem as PossibleExifValue<*>).valueReturnedByExifInterface
            val filteredPhotoList = allPhotosList.stream()
                .filter { v ->
                    v.readSingleExifInt(
                        exifParameterTagName,
                        Int.MIN_VALUE
                    ) == userIntExifValue
                }
                .collect(Collectors.toList())
            mRv_photos?.adapter = SearchRecycleViewAdapter(ArrayList(filteredPhotoList), listener)
            filteredPhotoList.clear()
        }
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
            allPhotosList,
            okButton
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
