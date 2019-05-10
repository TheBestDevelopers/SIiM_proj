package com.thebestdevelopers.exifphotogallery.fragments.search

import android.content.Context
import android.support.media.ExifInterface
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.thebestdevelopers.exifphotogallery.R
import com.thebestdevelopers.exifphotogallery.fragments.gallery.PhotoFile
import java.util.stream.Collectors


import android.support.v7.app.AlertDialog


class ExifSpinnerOnItemSelectedListener(
    private val context: Context,
    private val exifValue: EditText?,
    private val exifValueSpinner: Spinner?,
    private val mRv_photos: RecyclerView?,
    private val allPhotosAdapter: SearchRecycleViewAdapter,
    private val allPhotosList: ArrayList<PhotoFile>,
    private val okButton: Button?

) : AdapterView.OnItemSelectedListener {

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        mRv_photos?.adapter = allPhotosAdapter
        val exifParameter = p0?.getItemAtPosition(p2) as ExifParameter

        when (exifParameter.returnValueType) {
            Any::class -> onSpecialExifValueSelected(exifParameter.tagName, exifParameter.possibleValues)
            String::class -> onStringExifValueSelected(exifParameter.tagName)
            Int::class -> onIntExifValueSelected(exifParameter.tagName)
        }
    }

    private fun onSpecialExifValueSelected(tagName: String, possibleValues: ArrayList<PossibleExifValue<*>>?) {
        if (tagName == ExifInterface.TAG_DATETIME)
            onDateSelected()
        else
            onOrientationSelected(possibleValues)
    }

    private fun onStringExifValueSelected(paramTag: String) {
        hideExifValueAndShowExifValueSpinner()
        val allExifValues =
            allPhotosList.stream().map { photo -> photo.readSingleExifString(paramTag) }.distinct()
                .filter { x -> x != null }.collect(Collectors.toList())

        checkExistenceOfExifValues(allExifValues.isEmpty())
        allExifValues.add(context.getString(R.string.exif_null_value_desc))
        exifValueSpinner?.adapter =
            ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, allExifValues)
    }

    private fun onIntExifValueSelected(paramTag: String) {
        hideExifValueAndShowExifValueSpinner()
        val allExifValues =
            allPhotosList.stream().mapToInt { photo -> photo.readSingleExifInt(paramTag, Int.MIN_VALUE) }.distinct()
                .boxed().collect(Collectors.toList())
        allExifValues.sort()

        checkExistenceOfExifValues(allExifValues.count() == 1) // Int.MIN_VALUE nie jest filtrowane!!!

        val packedAllExifValues = ArrayList<IntegerExifValue>()
        allExifValues.forEach { x -> packedAllExifValues.add(IntegerExifValue(x, null)) }
        if (allExifValues.contains(Int.MIN_VALUE)) {
            packedAllExifValues.remove(IntegerExifValue(Int.MIN_VALUE, null))
            packedAllExifValues.add(IntegerExifValue(Int.MIN_VALUE, context.getString(R.string.exif_null_value_desc)))
        }

        exifValueSpinner?.adapter =
            ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, packedAllExifValues)
    }

    private fun checkExistenceOfExifValues(isEmpty: Boolean) {
        if (isEmpty) {
            AlertDialog.Builder(context)
                .setTitle("Nothing to see here!")
                .setMessage("Your photos do not have a value for the selected EXIF parameter")
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
            exifValueSpinner?.visibility = View.INVISIBLE
            okButton?.visibility = View.INVISIBLE
        }
    }

    private fun onOrientationSelected(possibleValues: ArrayList<PossibleExifValue<*>>?) {
        hideExifValueAndShowExifValueSpinner()
        exifValueSpinner?.adapter =
            ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, possibleValues)
    }

    private fun onDateSelected() {
        hideExifValueSpinnerAndShowExifValue()
    }

    private fun hideExifValueSpinnerAndShowExifValue() {
        exifValueSpinner?.visibility = View.GONE
        exifValue?.visibility = View.VISIBLE
        exifValue?.isFocusable = false
        okButton?.visibility = View.VISIBLE
    }

    private fun hideExifValueAndShowExifValueSpinner() {
        exifValue?.visibility = View.GONE
        exifValue?.text?.clear()
        exifValueSpinner?.visibility = View.VISIBLE
        okButton?.visibility = View.VISIBLE
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}