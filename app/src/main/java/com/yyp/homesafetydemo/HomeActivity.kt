package com.yyp.homesafetydemo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import kotlinx.android.synthetic.main.fragment_home.*

class HomeActivity : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    lateinit var mainHandler: Handler

    private val updateTextTask = object : Runnable {
        override fun run() {
            updateData()
            mainHandler.postDelayed(this, 2000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateData()
        mainHandler = Handler(Looper.getMainLooper())
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateTextTask)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateTextTask)
    }

    fun updateData() {
        println("Updating Data")

        val url = "http://192.168.88.100:8080/get"

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println("Success")
                println(body)

                val gson = GsonBuilder().create()
                val data = gson.fromJson(body, HomeData::class.java)

                activity?.runOnUiThread {
                    if (data.S1.Humid == -1) {
                        s1_connect.text = getString(R.string.home_offline)
                        s1_connect.setTextColor(resources.getColor(R.color.colorOffline))
                        s1_temp.text = "--°C"
                        s1_humid.text = "-- %"
                    } else {
                        s1_connect.text = getString(R.string.home_online)
                        s1_connect.setTextColor(resources.getColor(R.color.colorOnline))
                        s1_temp.text = String.format("%d°C", data.S1.Temp)
                        s1_humid.text = String.format("%d %%", data.S1.Humid)
                    }
                    if (data.S2.Humid == -1) {
                        s2_connect.text = getString(R.string.home_offline)
                        s2_connect.setTextColor(resources.getColor(R.color.colorOffline))
                        s2_temp.text = "--°C"
                        s2_humid.text = "-- %"
                    } else {
                        s2_connect.text = getString(R.string.home_online)
                        s2_connect.setTextColor(resources.getColor(R.color.colorOnline))
                        s2_temp.text = String.format("%d°C", data.S2.Temp)
                        s2_humid.text = String.format("%d %%", data.S2.Humid)
                    }
                    if (data.Gas.PM25 == -1) {
                        air_connect.text = getString(R.string.home_offline)
                        air_connect.setTextColor(resources.getColor(R.color.colorOffline))
                        pm25.text = "-- ug/m³"
                    } else {
                        air_connect.text = getString(R.string.home_online)
                        air_connect.setTextColor(resources.getColor(R.color.colorOnline))
                        pm25.text = String.format("%d ug/m³", data.Gas.PM25)
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failure")
                println(e.message)
            }
        })
    }
}

class HomeData(val S1: StationData, val S2: StationData, val Gas: GasData)

class StationData(val Temp: Int, val Humid: Int)

class GasData(val PM25: Int)