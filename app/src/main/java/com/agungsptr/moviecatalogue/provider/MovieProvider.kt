package com.agungsptr.moviecatalogue.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.agungsptr.moviecatalogue.db.DatabaseContract
import com.agungsptr.moviecatalogue.db.DatabaseContract.AUTHORITY
import com.agungsptr.moviecatalogue.db.MovieHelper
import com.agungsptr.moviecatalogue.db.TvShowHelper

class MovieProvider : ContentProvider() {

    companion object {
        private const val TABLE_MOVIE = DatabaseContract.MovieColumns.TABLE_NAME
        private val CONTENT_URI_MOVIE = DatabaseContract.MovieColumns.CONTENT_URI
        private const val TABLE_TV_SHOW = DatabaseContract.TvShowColumns.TABLE_NAME
        private val CONTENT_URI_TV_SHOW = DatabaseContract.TvShowColumns.CONTENT_URI

        private const val MOVIE = 1
        private const val MOVIE_ID = 2
        private const val TV_SHOW = 3
        private const val TV_SHOW_ID = 4

        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        private lateinit var movieHelper: MovieHelper
        private lateinit var tvShowHelper: TvShowHelper

        init {
            sUriMatcher.addURI(AUTHORITY, TABLE_MOVIE, MOVIE)
            sUriMatcher.addURI(AUTHORITY, "$TABLE_MOVIE/#", MOVIE_ID)
            sUriMatcher.addURI(AUTHORITY, TABLE_TV_SHOW, TV_SHOW)
            sUriMatcher.addURI(AUTHORITY, "$TABLE_TV_SHOW/#", TV_SHOW_ID)
        }
    }

    override fun onCreate(): Boolean {
        movieHelper = MovieHelper.getInstance(context as Context)
        movieHelper.open()

        tvShowHelper = TvShowHelper.getInstance(context as Context)
        tvShowHelper.open()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        var cursor: Cursor? = null
        when (sUriMatcher.match(uri)) {
            MOVIE -> cursor = movieHelper.queryAll()
            MOVIE_ID -> cursor = movieHelper.queryById(uri.lastPathSegment.toString())
            TV_SHOW -> cursor = tvShowHelper.queryAll()
        }
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val added: Long = when (MOVIE) {
            sUriMatcher.match(uri) -> movieHelper.insert(values)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI_MOVIE, null)
        return Uri.parse("$CONTENT_URI_MOVIE/$added")
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val updated: Int = when (MOVIE_ID) {
            sUriMatcher.match(uri) -> movieHelper.update(uri.lastPathSegment.toString(), values)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI_MOVIE, null)
        return updated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val deleted: Int = when (MOVIE_ID) {
            sUriMatcher.match(uri) -> movieHelper.deleteById(uri.lastPathSegment.toString())
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI_MOVIE, null)
        return deleted
    }
}
