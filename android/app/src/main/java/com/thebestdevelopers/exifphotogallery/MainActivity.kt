package com.thebestdevelopers.exifphotogallery

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.thebestdevelopers.exifphotogallery.fragments.gallery.GalleryFragment
import com.thebestdevelopers.exifphotogallery.fragments.search.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_gallery -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.mainFrame, GalleryFragment.newInstance(), getString(R.string.gallery_tag))
                    .addToBackStack(null)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.mainFrame, SearchFragment.newInstance(), getString(R.string.search_tag))
                    .addToBackStack(null)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_camera -> {
                launchCamera()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.mainFrame, GalleryFragment.newInstance(), getString(R.string.gallery_tag))
            .addToBackStack(null)
            .commit()
    }

    private fun launchCamera() {

    }
}
