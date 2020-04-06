package com.agungsptr.moviefavorite.helper

import android.database.Cursor
import com.agungsptr.moviefavorite.db.DatabaseContract
import com.agungsptr.moviefavorite.model.Movie
import com.agungsptr.moviefavorite.model.TvShow

object MappingHelper {

    fun mapMovieCursorToArrayList(movieCursor: Cursor?): ArrayList<Movie> {
        val movieList = ArrayList<Movie>()

        movieCursor?.apply {
            while (moveToNext()) {
                val movie = Movie()
                movie.id = getInt(getColumnIndexOrThrow(DatabaseContract.MovieColumns.ID))
                movie.title = getString(getColumnIndexOrThrow(DatabaseContract.MovieColumns.TITLE))
                movie.poster =
                    getString(getColumnIndexOrThrow(DatabaseContract.MovieColumns.POSTER))
                movieList.add(movie)
            }
        }
        return movieList
    }

    fun mapTvShowCursorToArrayList(tvShowCursor: Cursor?): ArrayList<TvShow> {
        val tvShowList = ArrayList<TvShow>()

        tvShowCursor?.apply {
            while (moveToNext()) {
                val tvShow = TvShow()
                tvShow.id = getInt(getColumnIndexOrThrow(DatabaseContract.TvShowColumns.ID))
                tvShow.title =
                    getString(getColumnIndexOrThrow(DatabaseContract.TvShowColumns.TITLE))
                tvShow.poster =
                    getString(getColumnIndexOrThrow(DatabaseContract.TvShowColumns.POSTER))
                tvShowList.add(tvShow)
            }
        }
        return tvShowList
    }
}