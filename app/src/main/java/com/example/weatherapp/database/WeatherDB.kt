package com.example.weatherapp.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.weatherapp.retrofit.DTO.WeatherDTO
import com.example.weatherapp.retrofit.DTO.WeatherResponseDTO
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet

class WeatherDB (context: Context){
    private val ID = "id"
    private val CITY_NAME = "city_name"
    private val TEMP = "temp"
    private val TEMP_MIN = "temp_min"
    private val TEMP_MAX = "temp_max"
    private val TIME = "time"
    private val DATE = "date"
    private val TABLE_NAME = "weather"
    private var database: Database? = null
    private var sqLiteDatabase: SQLiteDatabase? = null

    init {
        database = Database(context)
        sqLiteDatabase = database!!.writableDatabase
        createTable()
    }

    private fun createTable() {
        val DB_CREATE_SCRIPT = " CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" + ID + " INTEGER, " +
                CITY_NAME + " TEXT NOT NULL, " +
                TEMP + " REAL, " +
                TEMP_MIN + " REAL, " +
                TEMP_MAX + " REAL, " +
                TIME + " TEXT, " +
                DATE + " TEXT);"
        sqLiteDatabase!!.execSQL(DB_CREATE_SCRIPT)
    }

    fun closeDB() {
        if (sqLiteDatabase!!.isOpen)
            sqLiteDatabase!!.close()
    }

    fun clearTableDB() {
        sqLiteDatabase!!.execSQL(" DELETE FROM " + TABLE_NAME )
    }

    fun addWeather(weatherResponse: WeatherResponseDTO){
        val dateTime: Date = Calendar.getInstance().time
        val tf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val df = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val currentTime: String = tf.format(dateTime)
        val currentDate: String = df.format(dateTime)

        val query = " INSERT INTO " + TABLE_NAME + " VALUES(" + weatherResponse.id + ",\"" +
                weatherResponse.cityName + "\"," + weatherResponse.main!!.temp + "," +
                weatherResponse.main!!.tempMin + "," + weatherResponse.main!!.tempMax + ",\"" +
                currentTime + "\",\"" + currentDate + "\");"

        sqLiteDatabase!!.execSQL(query)
    }

    fun countWeatherByName(cityName: String): Int {
        var count = 0
        val query = " SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + CITY_NAME + "=\"" + cityName + "\""
        val cursor = sqLiteDatabase!!.rawQuery(query, null)
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("COUNT(*)"))
            cursor.close()
        }
        return count
    }

    fun getArrayWeather(): ArrayList<WeatherDTO>{
        val query = " SELECT * FROM " + TABLE_NAME + " GROUP BY " + ID
        val cursor = sqLiteDatabase!!.rawQuery(query, null)
        val arrayWeatherDTO = arrayListOf<WeatherDTO>()
        while (cursor.moveToNext()){
            arrayWeatherDTO.add(getWeather(cursor))
        }
        cursor.close()

        return arrayWeatherDTO
    }

    fun getWeatherByName(cityName: String): ArrayList<WeatherDTO> {
        val query = " SELECT * FROM " + TABLE_NAME + " WHERE " + CITY_NAME + "=\"" + cityName + "\""
        val cursor = sqLiteDatabase!!.rawQuery(query, null)
        val arrayWeatherDTO = arrayListOf<WeatherDTO>()
        while (cursor.moveToNext()) {
            arrayWeatherDTO.add(getWeather(cursor))
        }
        cursor.close()
        return arrayWeatherDTO
    }

    private fun getWeather(cursor: Cursor): WeatherDTO{
        val tempK = cursor.getDouble(cursor.getColumnIndex(TEMP))
        val tempMinK = cursor.getDouble(cursor.getColumnIndex(TEMP_MIN))
        val tempMaxK = cursor.getDouble(cursor.getColumnIndex(TEMP_MAX))

        val weather = WeatherDTO()
        weather.id = cursor.getInt(cursor.getColumnIndex(ID))
        weather.cityName = cursor.getString(cursor.getColumnIndex(CITY_NAME))
        weather.temp = (tempK.toInt() - 273.15) * 9/5 + 32
        weather.tempMin = (tempMinK.toInt() - 273.15) * 9/5 + 32
        weather.tempMax = (tempMaxK.toInt() - 273.15) * 9/5 + 32
        weather.time = cursor.getString(cursor.getColumnIndex(TIME))
        weather.date = cursor.getString(cursor.getColumnIndex(DATE))

        return weather
    }
}