package com.thebestdevelopers.exifphotogallery.fragments.search

import kotlin.reflect.KClass

data class ExifParameter(val name : String, val tagName : String, val valueType : KClass<out Any>){
    override fun toString(): String = name
}