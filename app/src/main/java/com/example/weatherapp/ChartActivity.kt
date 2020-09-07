package com.example.weatherapp

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.database.WeatherDB
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.activity_chart.*
import java.util.*
import kotlin.collections.ArrayList


class ChartActivity: AppCompatActivity() {

    private lateinit var weatherDB: WeatherDB
    var keyCityName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)
        val intent = intent
        keyCityName = intent.getStringExtra(getString(R.string.city_name_key))
        weatherDB = WeatherDB(this)
        val allWeather = weatherDB.getWeatherByName(keyCityName)

        val arrayTemp = ArrayList<Double>()
        val arrayTempMin = ArrayList<Double>()
        val arrayTempMax = ArrayList<Double>()
        val days = ArrayList<String>()
        val result: ArrayList<Entry> = ArrayList()
        val resultLineDataSet: LineDataSet
        val dataSet: ArrayList<ILineDataSet> = ArrayList()

        for (i in 0 until allWeather.size) {
            arrayTemp.add(allWeather[i].temp)
        }
        for (i in 0 until allWeather.size) {
            arrayTempMin.add(allWeather[i].tempMin)
        }
        for (i in 0 until allWeather.size) {
            arrayTempMax.add(allWeather[i].tempMax)
        }
        for (i in 0 until allWeather.size) {
            days.add(allWeather[i].date)
        }
        for (i in 0 until allWeather.size) {
            result.add(Entry(i.toFloat(), arrayTemp[i].toFloat()))
        }

        resultLineDataSet = LineDataSet(result, "")
        dataSet.add(resultLineDataSet)
        val chartData: LineData = LineData(dataSet)

        val formatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return days[value.toInt()]
            }
        }
        val xAxis: XAxis = graph_view.xAxis
        xAxis.granularity = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelRotationAngle = 70f
        xAxis.valueFormatter = formatter
        graph_view.data = chartData

        Log.d("tempMin", arrayTempMin.toString())
        Log.d("tempMax", arrayTempMax.toString())

        temp_min_value.text = getSmallest(arrayTempMin).toString()
        temp_max_value.text = getMax(arrayTempMax).toString()
    }

    private fun getSmallest(array: ArrayList<Double>): Double {
        var minValue = array[0]
        for (i in 0 until array.size) {
            if (array[i] < minValue) {
                minValue = array[i]
            }
        }
        return minValue
    }
    private fun getMax(array: ArrayList<Double>): Double {
        var maxValue = array[0]
        for (i in 0 until array.size) {
            if (array[i] > maxValue) {
                maxValue = array[i]
            }
        }
        return maxValue
    }
}