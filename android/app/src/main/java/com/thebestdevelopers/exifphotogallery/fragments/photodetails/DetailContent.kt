package com.thebestdevelopers.exifphotogallery.fragments.photodetails

object DetailContent {
    val ITEMS: MutableList<DetailItem> = ArrayList()

    fun clear() = ITEMS.clear()

    fun getDirtyList(): List<DetailItem> = ITEMS.filter { item -> item.isDirty() }

    fun addItems(tags: List<DetailsSettingsContent.Parameter> = DetailsSettingsContent.ITEMS, func : (String) -> String) =
        ITEMS.addAll(tags.map { parameter -> DetailItem(parameter, func(parameter.tag)) })

    data class DetailItem(val parameter : DetailsSettingsContent.Parameter,  var value : String, var originalValue: String = value){
        val description : String = parameter.tag.replace("(.)([A-Z])".toRegex(), "$1 $2") + ":"
        fun isDirty():Boolean = value != originalValue
    }
}