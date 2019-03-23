package com.thebestdevelopers.exifphotogallery.fragments.gallery

import android.media.ExifInterface

class PhotoFile(path: String) {
    var mPath = path

    fun extractExifData() {
        var exif = ExifInterface(mPath)
        //dane EXIF - możne tutaj je 'wydobyc'
    }
}