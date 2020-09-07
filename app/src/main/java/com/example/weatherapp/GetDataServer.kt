package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.weatherapp.database.WeatherDB
import com.example.weatherapp.retrofit.DTO.WeatherResponseDTO
import com.example.weatherapp.retrofit.RetrofitClientInstance
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class GetDataServer(private val context: Context) {
    private var weatherDB: WeatherDB
    init {
        weatherDB = WeatherDB(context)
    }

    fun getDataByCityName(cityName: String): Observable<Boolean>{
        return Observable.create(object: ObservableOnSubscribe<Boolean>{
            override fun subscribe(emitter: ObservableEmitter<Boolean>) {
                RetrofitClientInstance.getInstance()
                    .getWeatherByName(cityName)
                    .subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object: Observer<Response<WeatherResponseDTO>>{
                        override fun onComplete() {

                        }

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onNext(t: Response<WeatherResponseDTO>) {
                            if (t.code() == 200) {
                                val body = t.body()
                                weatherDB.addWeather(body!!)
                                val refresh = Intent(context, MainActivity::class.java)
                                refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                context.startActivity(refresh)
                            } else {
                                closeDB()
                            }
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                        }

                    })
            }

        })
    }

    fun getDataByCoords(lat: Double, lon: Double): Observable<Boolean> {
        return Observable.create(object: ObservableOnSubscribe<Boolean>{
            override fun subscribe(emitter: ObservableEmitter<Boolean>) {
                RetrofitClientInstance.getInstance()
                    .getWeatherByCoords(lat, lon)
                    .subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object: Observer<Response<WeatherResponseDTO>>{
                        override fun onComplete() {

                        }

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onNext(t: Response<WeatherResponseDTO>) {
                            if (t.code() == 200) {
                                val body = t.body()
                                weatherDB.addWeather(body!!)
                                val refresh = Intent(context, MainActivity::class.java)
                                refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                context.startActivity(refresh)
                            } else {
                                closeDB()
                            }
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                        }

                    })
            }

        })
    }

    private fun closeDB(){
        weatherDB.closeDB()
    }
    private fun clearDB(){
        weatherDB.clearTableDB()
    }
}