package com.example.weatherapp.retrofit.DTO

import com.google.gson.annotations.SerializedName

data class WeatherResponseDTO (
    @SerializedName("id") var id: Int = 0,
    @SerializedName("name") var cityName: String = "",
    @SerializedName("main") var main: WeatherResponseMainDTO? = null
)