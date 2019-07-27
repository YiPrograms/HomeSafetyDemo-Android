package com.yyp.homesafetydemo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
                    s1_temp.text = String.format("%d°C", data.S1.Temp)
                    s1_humid.text = String.format("%d %%", data.S1.Humid)
                    s2_temp.text = String.format("%d°C", data.S2.Temp)
                    s2_humid.text = String.format("%d %%", data.S2.Humid)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failure")
                println(e.message)
            }
        })
    }
}

class HomeData(val S1: StationData, val S2: StationData)

class StationData(val Temp: Int, val Humid: Int)