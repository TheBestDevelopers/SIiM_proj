package com.thebestdevelopers.exifphotogallery.fragments.photodetails

import android.support.media.ExifInterface

object DetailsSettingsContent {
    val ITEMS: MutableList<String> = ArrayList()
    val ALL_TAG: MutableList<String> = ArrayList()
    init {
        fill(
            ExifInterface.TAG_SUBFILE_TYPE,
            ExifInterface.TAG_IMAGE_WIDTH,
            ExifInterface.TAG_IMAGE_LENGTH,
            ExifInterface.TAG_COMPRESSION,
            ExifInterface.TAG_BITS_PER_SAMPLE,
            ExifInterface.TAG_PHOTOMETRIC_INTERPRETATION,
            ExifInterface.TAG_APERTURE_VALUE,
            ExifInterface.TAG_ARTIST,
            ExifInterface.TAG_ROWS_PER_STRIP,
            ExifInterface.TAG_STRIP_BYTE_COUNTS,
            ExifInterface.TAG_STRIP_OFFSETS,
            ExifInterface.TAG_SAMPLES_PER_PIXEL,
            ExifInterface.TAG_Y_RESOLUTION,
            ExifInterface.TAG_X_RESOLUTION,
            ExifInterface.TAG_PLANAR_CONFIGURATION,
            ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION,
            ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION,
            ExifInterface.TAG_RESOLUTION_UNIT
            )
    }

    fun clear():DetailsSettingsContent{
        ITEMS.clear()
        return this
    }
    fun fill() = ITEMS.addAll(ALL_TAG)

    fun add(tag: String) = ITEMS.add(tag)

    private fun fill(vararg tags: String){
        for (tag in tags) ALL_TAG.add(tag)
    }

}