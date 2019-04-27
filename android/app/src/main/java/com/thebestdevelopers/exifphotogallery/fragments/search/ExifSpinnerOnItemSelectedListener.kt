package com.thebestdevelopers.exifphotogallery.fragments.search

import android.content.Context
import android.support.media.ExifInterface
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.thebestdevelopers.exifphotogallery.R

class ExifSpinnerOnItemSelectedListener(
    private val context: Context,
    private val exifValue: EditText?,
    private val exifValueSpinner: Spinner?,
    private val mRv_photos: RecyclerView?,
    private val allPhotosAdapter: SearchRecycleViewAdapter
) : AdapterView.OnItemSelectedListener {

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        mRv_photos?.adapter = allPhotosAdapter

        val exifParameter = p0?.getItemAtPosition(p2) as ExifParameter

        when (exifParameter.tagName) {
            ExifInterface.TAG_DATETIME -> onDateSelected()
            ExifInterface.TAG_ORIENTATION -> onOrientationSelected()
        }
    }

    private fun onOrientationSelected() {
        exifValue?.visibility = View.GONE
        exifValue?.text?.clear()
        exifValueSpinner?.visibility = View.VISIBLE

        val adapter =
            ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, createExifOrientationValueList())
        exifValueSpinner?.adapter = adapter
    }

    private fun onDateSelected() {
        exifValueSpinner?.visibility = View.GONE
        exifValue?.visibility = View.VISIBLE
        exifValue?.isFocusable = false
    }

    private fun createExifOrientationValueList(): ArrayList<ExifOrientationValue> {
        val exifOrientationValues = ArrayList<ExifOrientationValue>()
        var item = ExifOrientationValue(0, "horizontally")
        exifOrientationValues.add(item)
        item = ExifOrientationValue(90, "vertically")
        exifOrientationValues.add(item)
        item = ExifOrientationValue(180, "upside down")
        exifOrientationValues.add(item)
        item = ExifOrientationValue(270, "strange")
        exifOrientationValues.add(item)
        return exifOrientationValues
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}