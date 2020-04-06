package com.agungsptr.moviecatalogue.db

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {

    const val AUTHORITY = "com.agungsptr.moviecatalogue"
    const val SCHEME = "content"

    class MovieColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "moviefav"
            const val ID = "_id"
            const val POSTER = "poster"
            const val TITLE = "title"

            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }

    class TvShowColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "tvshowfav"
            const val ID = "_id"
            const val POSTER = "poster"
            const val TITLE = "title"

            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(MovieColumns.TABLE_NAME)
                .build()
        }
    }
}