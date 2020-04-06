package com.agungsptr.moviecatalogue.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.sql.SQLException

class TvShowHelper(context: Context) {

    private var databaseHelper: DatabaseHelper = DatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    companion object {
        private const val DATABASE_TABLE = DatabaseContract.TvShowColumns.TABLE_NAME
        private var INSTANCE: TvShowHelper? = null

        fun getInstance(context: Context): TvShowHelper = INSTANCE ?: synchronized(this) {
            INSTANCE ?: TvShowHelper(context)
        }
    }

    @Throws(SQLException::class)
    fun open() {
        database = databaseHelper.writableDatabase
    }

    fun close() {
        databaseHelper.close()
        if (database.isOpen)
            database.close()
    }

    fun queryAll(): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseContract.TvShowColumns.ID} ASC"
        )
    }

    fun queryById(id: String): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            "${DatabaseContract.TvShowColumns.ID} = ?",
            arrayOf(id),
            null,
            null,
            null,
            null
        )
    }

    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    fun update(id: String, values: ContentValues?): Int {
        return database.update(
            DATABASE_TABLE,
            values,
            "${DatabaseContract.TvShowColumns.ID} = ?",
            arrayOf(id)
        )
    }

    fun deleteById(id: String): Int {
        return database.delete(
            DATABASE_TABLE,
            "${DatabaseContract.TvShowColumns.ID} = '$id'",
            null
        )
    }
}