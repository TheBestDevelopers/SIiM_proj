package com.thebestdevelopers.exifphotogallery.fragments.photodetails

import android.support.media.ExifInterface
import android.text.InputType

object DetailsSettingsContent {
    val ITEMS: ArrayList<Parameter> = ArrayList()

    val ORIENTATION_VALUE: ArrayList<ListElement> = fill(
        ListElement("undefined", 0), //ExifInterface.ORIENTATION_UNDEFINED 0)
        ListElement("normal", 1),//ExifInterface.ORIENTATION_NORMAL) 1
        ListElement("flip horizontal", 2), //ExifInterface.ORIENTATION_FLIP_HORIZONTAL) 2
        ListElement("rotate 180",3) ,//ExifInterface.ORIENTATION_ROTATE_180) 3
        ListElement("flip vertical", 4) ,//ExifInterface.ORIENTATION_FLIP_VERTICAL) 4
        ListElement("transpose", 5) ,//ExifInterface.ORIENTATION_TRANSPOSE) 5
        ListElement("rotate 90", 6) ,//ExifInterface.ORIENTATION_ROTATE_90) 6
        ListElement("transverse", 7) ,//ExifInterface.ORIENTATION_TRANSVERSE) 7
        ListElement("rotate 270", 8) //ExifInterface.ORIENTATION_ROTATE_270), 8
    )
    val RESOLUTION_UNIT_VALUE: ArrayList<ListElement> = fill(
        ListElement("inches", 2),
        ListElement("cm", 3)
    )
    val COMPRESION_VALUE: ArrayList<ListElement> = fill(
        ListElement("Uncompressed", 1),
        ListElement("CCITT 1D", 2),
        ListElement("T4/Group 3 Fax", 3),
        ListElement("T6, Group 4 Fax", 4),
        ListElement("LZW", 5),
        ListElement("JPEG (old-style)", 6),
        ListElement("JPEG", 7),
        ListElement("Adobe Deflate", 8),
        ListElement("JBIG B&W", 9)
    )
    val ALL_TAG: ArrayList<Parameter> = fill(
        Parameter(ExifInterface.TAG_SUBFILE_TYPE, InputType.TYPE_CLASS_TEXT),
        Parameter(ExifInterface.TAG_DATETIME, InputType.TYPE_CLASS_DATETIME),
        Parameter(ExifInterface.TAG_RESOLUTION_UNIT, InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE, RESOLUTION_UNIT_VALUE),
        Parameter(ExifInterface.TAG_ORIENTATION, InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE, ORIENTATION_VALUE),
        Parameter(ExifInterface.TAG_IMAGE_WIDTH, InputType.TYPE_CLASS_NUMBER),
        Parameter(ExifInterface.TAG_IMAGE_LENGTH, InputType.TYPE_CLASS_NUMBER),
        Parameter(ExifInterface.TAG_COMPRESSION, InputType.TYPE_CLASS_NUMBER),
        Parameter(ExifInterface.TAG_BITS_PER_SAMPLE, InputType.TYPE_CLASS_NUMBER),
        Parameter(ExifInterface.TAG_PHOTOMETRIC_INTERPRETATION, InputType.TYPE_CLASS_NUMBER),
        Parameter(ExifInterface.TAG_APERTURE_VALUE, InputType.TYPE_NUMBER_FLAG_DECIMAL),
        Parameter(ExifInterface.TAG_ARTIST,InputType.TYPE_CLASS_TEXT),
        Parameter(ExifInterface.TAG_ROWS_PER_STRIP, InputType.TYPE_CLASS_NUMBER),
        Parameter(ExifInterface.TAG_STRIP_BYTE_COUNTS, InputType.TYPE_CLASS_NUMBER),
        Parameter(ExifInterface.TAG_STRIP_OFFSETS, InputType.TYPE_CLASS_NUMBER),
        Parameter(ExifInterface.TAG_SAMPLES_PER_PIXEL, InputType.TYPE_CLASS_NUMBER),
        Parameter(ExifInterface.TAG_Y_RESOLUTION, InputType.TYPE_CLASS_NUMBER),
        Parameter(ExifInterface.TAG_X_RESOLUTION, InputType.TYPE_CLASS_NUMBER),
        Parameter(ExifInterface.TAG_PLANAR_CONFIGURATION, InputType.TYPE_CLASS_NUMBER),
        Parameter(ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION, InputType.TYPE_CLASS_NUMBER),
        Parameter(ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION, InputType.TYPE_CLASS_NUMBER)
    )

    fun clear():DetailsSettingsContent{
        ITEMS.clear()
        return this
    }
    fun fill() = ITEMS.addAll(ALL_TAG)

    private fun <T>fill(vararg tags: T): ArrayList<T>{
        val list = ArrayList<T>()
        for (tag in tags) list.add(tag)
        return list
    }

    data class ListElement(val name: String, val index: Int){
        override fun toString(): String {
            return name
        }
    }

    data class Parameter(val tag: String, val type: Int, val values: ArrayList<ListElement>? = null)
}