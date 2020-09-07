package com.example.weatherapp.retrofit.DTO

data class WeatherDTO (
    var id: Int = 0,
    var cityName: String = "",
    var temp: Double = 0.0,
    var tempMin: Double = 0.0,
    var tempMax: Double = 0.0,
    var time: String = "",
    var date: String = "",
    var unit: String = "F"
)