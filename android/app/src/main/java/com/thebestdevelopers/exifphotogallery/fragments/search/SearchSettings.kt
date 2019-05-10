package com.thebestdevelopers.exifphotogallery.fragments.search

import android.support.media.ExifInterface

object SearchSettings {

    private val EXIF_ORIENTATION_VALUES: ArrayList<PossibleExifValue<*>>? = arrayListOf(
        PossibleExifValue("undefined", ExifInterface.ORIENTATION_UNDEFINED),
        PossibleExifValue("normal", ExifInterface.ORIENTATION_NORMAL),
        PossibleExifValue("flip horizontal", ExifInterface.ORIENTATION_FLIP_HORIZONTAL),
        PossibleExifValue("rotate 180", ExifInterface.ORIENTATION_ROTATE_180),
        PossibleExifValue("flip vertical", ExifInterface.ORIENTATION_FLIP_VERTICAL),
        PossibleExifValue("transpose", ExifInterface.ORIENTATION_TRANSPOSE),
        PossibleExifValue("rotate 90", ExifInterface.ORIENTATION_ROTATE_90),
        PossibleExifValue("transverse", ExifInterface.ORIENTATION_TRANSVERSE),
        PossibleExifValue("rotate 270", ExifInterface.ORIENTATION_ROTATE_270),
        PossibleExifValue("empty", Int.MIN_VALUE)
    )

    private val EXIF_COMPRESSION_VALUES: ArrayList<PossibleExifValue<*>>? = arrayListOf(
        PossibleExifValue("Uncompressed", 1),
        PossibleExifValue("CCITT 1D", 2),
        PossibleExifValue("T4/Group 3 Fax", 3),
        PossibleExifValue("T6, Group 4 Fax", 4),
        PossibleExifValue("LZW", 5),
        PossibleExifValue("JPEG (old-style)", 6),
        PossibleExifValue("JPEG", 7),
        PossibleExifValue("Adobe Deflate", 8),
        PossibleExifValue("JBIG B&W", 9),
        PossibleExifValue("empty", Int.MIN_VALUE)
    )

    private val EXIF_RESOLUTION_UNIT_VALUES: ArrayList<PossibleExifValue<*>>? = arrayListOf(
        PossibleExifValue("inches", 2),
        PossibleExifValue("cm", 3),
        PossibleExifValue("empty", Int.MIN_VALUE)
    )

    val EXIF_PARAMETER_LIST = arrayListOf(
        ExifParameter("Date", ExifInterface.TAG_DATETIME, Any::class),
        ExifParameter("Orientation", ExifInterface.TAG_ORIENTATION, Any::class, EXIF_ORIENTATION_VALUES),
        ExifParameter("ISO speed", ExifInterface.TAG_ISO_SPEED_RATINGS, Int::class),
        ExifParameter("Subfile type", ExifInterface.TAG_SUBFILE_TYPE, String::class),
        ExifParameter("Image width", ExifInterface.TAG_IMAGE_WIDTH, Int::class),
        ExifParameter("Image length", ExifInterface.TAG_IMAGE_LENGTH, Int::class),
        ExifParameter("Compression", ExifInterface.TAG_COMPRESSION, Any::class, EXIF_COMPRESSION_VALUES),
        ExifParameter("Bits per sample", ExifInterface.TAG_BITS_PER_SAMPLE, Int::class),
        ExifParameter("Photometric inter.", ExifInterface.TAG_PHOTOMETRIC_INTERPRETATION, Int::class),
        ExifParameter("Aperture value", ExifInterface.TAG_APERTURE_VALUE, String::class),
        ExifParameter("Artist", ExifInterface.TAG_ARTIST, String::class),
        ExifParameter("Rows per strip", ExifInterface.TAG_ROWS_PER_STRIP, Int::class),
        ExifParameter("Strip byte counts", ExifInterface.TAG_STRIP_BYTE_COUNTS, Int::class),
        ExifParameter("Strip offsets", ExifInterface.TAG_STRIP_OFFSETS, Int::class),
        ExifParameter("Samp. per pixel", ExifInterface.TAG_SAMPLES_PER_PIXEL, Int::class),
        ExifParameter("Y resolution", ExifInterface.TAG_Y_RESOLUTION, String::class),
        ExifParameter("X resolution", ExifInterface.TAG_X_RESOLUTION, String::class),
        ExifParameter("Planar config.", ExifInterface.TAG_PLANAR_CONFIGURATION, Int::class),
        ExifParameter("FC Y resolution", ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION, String::class),
        ExifParameter("FC X resolution", ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION, String::class),
        ExifParameter("Resolution unit", ExifInterface.TAG_RESOLUTION_UNIT, Any::class, EXIF_RESOLUTION_UNIT_VALUES)
    )
}