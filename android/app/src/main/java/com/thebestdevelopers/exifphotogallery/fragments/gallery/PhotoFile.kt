package com.thebestdevelopers.exifphotogallery.fragments.gallery

import android.support.media.ExifInterface

class PhotoFile(path: String) {
    var mPath = path

    fun extractExifData() {
        var exif = ExifInterface(mPath)
        //dane EXIF - możne tutaj je 'wydobyc'
    }

    /*
        @param exifParameter - the parameter you are looking for e.g. ExifInterface.TAG_DATETIME
        @return value of the parameter you are looking for
     */
    fun readSingleExif(exifParameter: String) : String?{
        val exif = ExifInterface(mPath)
        return exif.getAttribute(exifParameter)
    }
}