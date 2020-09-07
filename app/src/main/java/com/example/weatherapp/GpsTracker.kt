package com.example.weatherapp

import android.Manifest
import android.content.Intent
import android.os.IBinder
import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.location.LocationManager
import android.app.Activity
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.util.Log


internal class GpsTracker(private val mContext: Context) : Service(), LocationListener {

    var isGPSEnabled = false
    var isNetworkEnabled = false
    var canGetLocation = false

    var location: Location? = null
    var currentLatitude: Double = 0.toDouble()
    var currentLongitude: Double = 0.toDouble()

    protected var locationManager: LocationManager? = null

    init {
        getCurrentLocation()
    }

    fun getCurrentLocation(): Location? {
        try {
            locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

            isNetworkEnabled = locationManager!!
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnabled && !isNetworkEnabled) {
                showSettingsAlert()
            } else {
                this.canGetLocation = true
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            mContext as Activity,
                            arrayOf(
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            101
                        )
                    }
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this
                    )

                    Log.d("Network", "Network")
                    if (locationManager != null) {
                        location = locationManager!!
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                        if (location != null) {
                            currentLatitude = location!!.latitude
                            currentLongitude = location!!.longitude
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        if (ActivityCompat.checkSelfPermission(
                                mContext,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                mContext,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                mContext as Activity,
                                arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ),
                                101
                            )
                        }
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this
                        )

                        Log.d("GPS Enabled", "GPS Enabled")
                        if (locationManager != null) {
                            location = locationManager!!
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER)

                            if (location != null) {
                                currentLatitude = location!!.latitude
                                currentLongitude = location!!.longitude
                            }
                        }
                    }
                } else {
                    showSettingsAlert()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return location
    }

    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@GpsTracker)
        }
    }

    fun canGetLocation(): Boolean {
        return this.canGetLocation
    }

    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext)
        alertDialog.setTitle("GPS is settings")
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")

        alertDialog.setPositiveButton("Settings") { dialog, which ->
            val intent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
            mContext.startActivity(intent)
        }

        alertDialog.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    override fun onLocationChanged(location: Location) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    companion object {
        private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // 10 meters

        private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute
    }
}