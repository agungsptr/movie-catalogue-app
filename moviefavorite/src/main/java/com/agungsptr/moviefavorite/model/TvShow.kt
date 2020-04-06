package com.agungsptr.moviefavorite.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Suppress("PLUGIN_WARNING")
@Parcelize
class TvShow : Parcelable {
    var id: Int = 0
    var title: String? = null
    var description: String? = null
    var poster: String? = null
    var year: String? = null
    var score: String? = null
    var backdrop: String? = null
}