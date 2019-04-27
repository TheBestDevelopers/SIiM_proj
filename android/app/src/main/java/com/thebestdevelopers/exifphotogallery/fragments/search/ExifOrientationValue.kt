package com.thebestdevelopers.exifphotogallery.fragments.search

data class ExifOrientationValue(val value : Int, val description : String) {
    override fun toString(): String {
        return "$value - $description"
    }
}