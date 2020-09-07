package com.example.weatherapp.retrofit

import com.example.weatherapp.retrofit.DTO.WeatherResponseDTO
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClientInstance {
    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private val API_KEY = "ab30a33c2ee32db5dc876d5c7a438125"
    private var getRequest: GetRequest? = null

    init {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        getRequest = retrofit.create(GetRequest::class.java)
    }

    fun getWeatherByName(cityName: String): Observable<Response<WeatherResponseDTO>> = getRequest!!.getWeatherByName(cityName, API_KEY)

    fun getWeatherByCoords(lat: Double, lon: Double): Observable<Response<WeatherResponseDTO>> = getRequest!!.getWeatherByCoords(lat, lon, API_KEY)

    companion object {
        private var instance: RetrofitClientInstance? = null
        fun getInstance(): RetrofitClientInstance {
            if (instance == null) {
                instance = RetrofitClientInstance()
            }

            return instance as RetrofitClientInstance
        }
    }
}