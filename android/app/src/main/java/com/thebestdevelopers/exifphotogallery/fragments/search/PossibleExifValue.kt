package com.thebestdevelopers.exifphotogallery.fragments.search

data class PossibleExifValue<T>(val description : String, val valueReturnedByExifInterface : T) {
    override fun toString(): String {
        return description
    }
}