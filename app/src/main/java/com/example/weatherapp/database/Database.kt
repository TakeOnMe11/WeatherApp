package com.example.weatherapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database(val context: Context): SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VERSION
){
    override fun onCreate(p0: SQLiteDatabase?) {

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun deleteDB(){
        context.deleteDatabase(DATABASE_NAME)
    }

    companion object {
        val DATABASE_NAME = "dbWeatherApp.db"
        private val DATABASE_VERSION = 1
    }
}