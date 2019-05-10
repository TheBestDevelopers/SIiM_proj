package com.thebestdevelopers.exifphotogallery.fragments.search

import kotlin.reflect.KClass

data class ExifParameter(val name : String, val tagName : String, val returnValueType : KClass<out Any>, val possibleValues: ArrayList<PossibleExifValue<*>>? = null){
    override fun toString(): String = name
}