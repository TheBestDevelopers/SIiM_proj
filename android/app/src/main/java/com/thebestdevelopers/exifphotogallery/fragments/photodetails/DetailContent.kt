package com.thebestdevelopers.exifphotogallery.fragments.photodetails

object DetailContent {
    val ITEMS: MutableList<DetailItem> = ArrayList()

    fun clear() = ITEMS.clear()

    fun getDirtyList(): List<DetailItem> = ITEMS.filter { item -> item.isDirty() }

    fun addItems(tags: List<String> = DetailsSettingsContent.ITEMS, func : (String) -> String) =
        ITEMS.addAll(tags.map { tag -> DetailItem(tag, func(tag)) })

    fun addItem(tag: String, func: (String) -> String) = ITEMS.add(DetailItem(tag, func(tag)))

    data class DetailItem(val tag : String,  var value : String, var originalValue: String = value){
        val description : String = tag.replace("(.)([A-Z])".toRegex(), "$1 $2") + ":"
        fun isDirty():Boolean = value != originalValue
    }
}