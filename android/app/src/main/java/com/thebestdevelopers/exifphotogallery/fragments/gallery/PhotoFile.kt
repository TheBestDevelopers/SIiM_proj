package com.thebestdevelopers.exifphotogallery.fragments.gallery

import android.media.ExifInterface

class PhotoFile(path: String) {
    var mPath = path
    var soft = ""

    fun extractExifData() {
        var exif = ExifInterface(mPath)
        //pobranie wszystkich danych exif
        //soft = exif.getAttribute(ExifInterface.TAG_SOFTWARE)
    }

    fun getSoftTAG() = soft
}