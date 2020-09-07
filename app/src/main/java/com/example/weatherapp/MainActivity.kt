package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.database.WeatherDB
import com.example.weatherapp.retrofit.DTO.WeatherDTO
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.weather_list_item.*
import java.io.IOException
import java.lang.IndexOutOfBoundsException
import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var viewAdapter: WeatherListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var weatherDB: WeatherDB
    private lateinit var listWeather: ArrayList<WeatherDTO>

    private lateinit var fusedLocationProvider: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherDB = WeatherDB(this)
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        listWeather = weatherDB.getArrayWeather()
        viewAdapter = WeatherListAdapter(listWeather, this)
        viewManager = LinearLayoutManager(this)
        rv_weather_list.apply {
            layoutManager = viewManager
            adapter = viewAdapter
            setHasFixedSize(true)
        }
        Log.d("listWeather", listWeather.toString())
        var lastWeather: WeatherDTO?
        try {
            lastWeather = listWeather.last()
        } catch (e: NoSuchElementException) {
            lastWeather = null
        }
        Log.d("lastWeather", lastWeather.toString())
        val tempCels: ArrayList<Double> = ArrayList()
        val tempFahr: ArrayList<Double> = ArrayList()
        try {
            for (i in 0 until listWeather.size) {
                tempCels.add((listWeather[i].temp - 32) * 5 / 9)
            }
            for (i in 0 until listWeather.size) {
                tempFahr.add(listWeather[i].temp)
            }
        } catch (e: IndexOutOfBoundsException) {}


        var tempCelsLast: Double?
        var tempFahrLast: Double?
        try {
            tempCelsLast = tempCels.last()
        } catch (e: NoSuchElementException) {
            tempCelsLast = null
        }
        try {
            tempFahrLast = tempFahr.last()
        } catch (e: NoSuchElementException) {
            tempFahrLast = null
        }
        Log.d("tempCels", tempCels.toString())
        Log.d("tempFahr", tempFahr.toString())
        tv_city_name.text = lastWeather?.cityName
        if (lastWeather?.temp == null) {
            tv_temp.text = ""
        } else {
            tv_temp.text = lastWeather.temp.toInt().toString()
        }

        switch_ftoc.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p1) {
                    if (tempCelsLast != null) {
                        tv_temp.text = tempCelsLast.toInt().toString()
                    } else {
                        tv_temp.text = ""
                    }
                    try {
                        for (i in 0 until listWeather.size) {
                            listWeather[i].temp = tempCels[i]
                            listWeather[i].unit = getString(R.string.celsius)
                        }
                    } catch (e: IndexOutOfBoundsException){}
                    viewAdapter.notifyDataSetChanged()
                } else if (!p1) {
                    if (tempFahrLast != null) {
                        tv_temp.text = tempFahrLast.toInt().toString()
                    } else {
                        tv_temp.text = ""
                    }
                    try {
                        for (i in 0 until listWeather.size) {
                            listWeather[i].temp = tempFahr[i]
                            listWeather[i].unit = getString(R.string.fahrenheit)
                        }
                    } catch (e: IndexOutOfBoundsException) {}
                    viewAdapter.notifyDataSetChanged()
                }
            }
        })
        if (tempCelsLast != null) {
            when {
                tempCelsLast < 10 -> weather_main_window.background = this.getDrawable(R.color.coldWeather)
                tempCelsLast in 10.0..25.0 -> weather_main_window.background = this.getDrawable(R.color.normalWeather)
                tempCelsLast > 25 -> weather_main_window.background = this.getDrawable(R.color.hotWeather)
            }
        } else {
            weather_main_window.background = this.getDrawable(R.color.normalWeather)
        }
    }


    private fun getWeatherByCityName(cityName: String) {
        val getDataServer = GetDataServer(this)
        getDataServer.getDataByCityName(cityName)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object: Observer<Boolean> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Boolean) {
                    listWeather = weatherDB.getArrayWeather()
                    viewAdapter.notifyDataSetChanged()
                }

                override fun onError(e: Throwable) {

                }

            })
    }

    private fun getWeatherByCoords(lat: Double, lon: Double) {
        val getDataServer = GetDataServer(this)
        getDataServer.getDataByCoords(lat, lon)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object: Observer<Boolean>{
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Boolean) {
                    listWeather = weatherDB.getArrayWeather()
                    viewAdapter.notifyDataSetChanged()
                }

                override fun onError(e: Throwable) {

                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)

        val item = menu!!.findItem(R.id.menuSearch)
        val searchView: SearchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                Log.d("tag", "tag")
                if (p0 != null) {
                    p0.replace(" ", "")
                    getWeatherByCityName(p0)
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSearch -> {
                val searchView: SearchView = item.actionView as SearchView
                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        if (p0 != null) {
                            p0.replace(" ", "")
                            getWeatherByCityName(p0)
                        }
                        searchView.clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(p0: String?): Boolean {
                        return true
                    }

                })
            }
            R.id.geopos -> {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    val gpsTracker = GpsTracker(this)
                    if (gpsTracker.canGetLocation()) {
                        val arrayCoords = getLocation()
                        val lat = arrayCoords.first()
                        val lon = arrayCoords.last()
                        getWeatherByCoords(lat, lon)
                    } else {
                        gpsTracker.showSettingsAlert()
                    }
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
                }
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getLocation(): ArrayList<Double> {
        val arrayList = ArrayList<Double>()

        val gpsTracker = GpsTracker(this)
        if (gpsTracker.canGetLocation()) {
            val lat = gpsTracker.currentLatitude
            val lon = gpsTracker.currentLongitude
            arrayList.add(lat)
            arrayList.add(lon)
        } else {
            gpsTracker.showSettingsAlert()
        }
        return arrayList
    }

    override fun onDestroy() {
        finish()
        super.onDestroy()
    }

}
