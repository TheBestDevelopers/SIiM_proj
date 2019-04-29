package com.thebestdevelopers.exifphotogallery.fragments.search

data class IntegerExifValue(var value : Int, val description : String? ) {
    override fun toString(): String = description ?: value.toString()

    override fun hashCode(): Int {
        var result = value
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IntegerExifValue

        if (value != other.value) return false

        return true
    }
}