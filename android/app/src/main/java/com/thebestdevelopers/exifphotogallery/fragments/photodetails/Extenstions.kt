package com.thebestdevelopers.exifphotogallery.fragments.photodetails

import android.support.annotation.LayoutRes
import android.support.media.ExifInterface
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false):View{
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)