package com.thebestdevelopers.exifphotogallery.fragments.gallery

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.thebestdevelopers.exifphotogallery.R
import java.io.File

class GalleryRecyclerViewAdapter(photoList: ArrayList<PhotoFile>) :
    RecyclerView.Adapter<GalleryRecyclerViewAdapter.CustomViewHolder>() {

    var photos = photoList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val photo = photos[position]
        Picasso.get().load(File(photo.mPath)).into(holder.imageView)
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView = itemView.findViewById<ImageView>(R.id.iv_photo)
    }
}