package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.database.WeatherDB
import com.example.weatherapp.retrofit.DTO.WeatherDTO
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.weather_list_item.view.*

class WeatherListAdapter(private val arrayWeather: ArrayList<WeatherDTO>, val context: Context): RecyclerView.Adapter<WeatherListAdapter.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weather_list_item, parent, false) as View
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return arrayWeather.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(arrayWeather[position])
    }

    inner class Holder(v: View): RecyclerView.ViewHolder(v) {

        val weatherDB = WeatherDB(context)
        var cityName = ""

        fun bind(weatherDTO: WeatherDTO) = with(itemView){
            list_city_name_temp.text = weatherDTO.cityName + ", " + weatherDTO.temp.toInt() + " " + weatherDTO.unit
            list_weather_datetime.text = weatherDTO.date + " " + weatherDTO.time
            if (weatherDB.countWeatherByName(weatherDTO.cityName) > 1) {
                list_chart_icon.visibility = View.VISIBLE
            }
            list_chart_icon.setOnClickListener {
                cityName = weatherDTO.cityName
                val intent = Intent(context, ChartActivity::class.java)
                intent.putExtra(context.getString(R.string.city_name_key), cityName)
                context.startActivity(intent)
            }
        }

    }

}