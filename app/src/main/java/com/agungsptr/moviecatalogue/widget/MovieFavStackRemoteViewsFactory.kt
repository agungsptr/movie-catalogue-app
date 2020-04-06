package com.agungsptr.moviecatalogue.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.agungsptr.moviecatalogue.R
import com.agungsptr.moviecatalogue.db.MovieHelper
import com.agungsptr.moviecatalogue.helper.MappingHelper
import com.bumptech.glide.Glide

internal class MovieFavStackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<Bitmap>()
    private lateinit var movieHelper: MovieHelper

    override fun onCreate() {

    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getItemId(position: Int): Long = 0

    override fun onDataSetChanged() {
        mWidgetItems.clear()

        movieHelper = MovieHelper.getInstance(mContext)
        movieHelper.open()

        val cursor = movieHelper.queryAll()
        val movies = MappingHelper.mapMovieCursorToArrayList(cursor)

        for (movie in movies) {
            val img = Glide.with(mContext)
                .asBitmap()
                .load(movie.poster)
                .submit(400, 600)
            mWidgetItems.add(img.get())
            Glide.with(mContext).clear(img)
        }

        movieHelper.close()
    }

    override fun hasStableIds(): Boolean = false

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(
            mContext.packageName,
            R.layout.item_widget
        )

        if (mWidgetItems.isEmpty()) return rv

        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])
        val extras = bundleOf(
            MovieFavoriteWidget.EXTRA_ITEM to position
        )

        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)

        return rv
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewTypeCount(): Int = 1

    override fun onDestroy() {

    }
}