package com.agungsptr.moviecatalogue.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "dbmoviecatalogue"
        private const val DATABASE_VERSION = 1
        private const val SQL_CREATE_TABLE_MOVIEFAV =
            "CREATE TABLE ${DatabaseContract.MovieColumns.TABLE_NAME} (" +
                    "${DatabaseContract.MovieColumns.ID} INTEGER PRIMARY KEY," +
                    "${DatabaseContract.MovieColumns.TITLE} TEXT NOT NULL," +
                    "${DatabaseContract.MovieColumns.POSTER} TEXT NOT NULL)"
        private const val SQL_CREATE_TABLE_TVSHOWFAV =
            "CREATE TABLE ${DatabaseContract.TvShowColumns.TABLE_NAME} (" +
                    "${DatabaseContract.TvShowColumns.ID} INTEGER PRIMARY KEY," +
                    "${DatabaseContract.TvShowColumns.TITLE} TEXT NOT NULL," +
                    "${DatabaseContract.TvShowColumns.POSTER} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_TABLE_MOVIEFAV)
        db?.execSQL(SQL_CREATE_TABLE_TVSHOWFAV)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.MovieColumns.TABLE_NAME}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.TvShowColumns.TABLE_NAME}")
        onCreate(db)
    }

}