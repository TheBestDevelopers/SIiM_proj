package com.thebestdevelopers.exifphotogallery.fragments.search

data class ExifParameter(val name : String, val tagName : String){
    override fun toString(): String {
        return name
    }
}