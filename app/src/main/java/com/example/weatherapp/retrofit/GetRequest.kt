package com.example.weatherapp.retrofit

import com.example.weatherapp.retrofit.DTO.WeatherResponseDTO
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GetRequest {
    @GET("weather")
    fun getWeatherByName(@Query(value = "q") cityName: String, @Query("appid") key: String): Observable<Response<WeatherResponseDTO>>

    @GET("weather")
    fun getWeatherByCoords(@Query(value = "lat") lat: Double, @Query("lon") lon: Double, @Query("appid") key: String): Observable<Response<WeatherResponseDTO>>
}