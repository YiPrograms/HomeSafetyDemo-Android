package com.yyp.homesafetydemo

import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity

import com.yyp.homesafetydemo.ui.main.SectionsPagerAdapter
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder

import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_alerts.*
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        mainHandler = Handler(Looper.getMainLooper())
    }

    lateinit var mainHandler: Handler

    private val updateTextTask = object : Runnable {
        override fun run() {
            updateData()
            mainHandler.postDelayed(this, 2000)
        }
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateTextTask)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateTextTask)
    }


    var alertLastCount: Int = 0

    fun updateData() {
        println("Updating Data")

        val url = "http://homesafetydemo.ml:8080/get"

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println("Success")
                println(body)

                val gson = GsonBuilder().create()
                val data = gson.fromJson(body, HomeData::class.java)

                runOnUiThread {
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
                if (data.Alerts.isEmpty()) {
                    updateList(listOf(Alert("No Alerts", "", -1)))
                } else {
                    if (data.Alerts.size != alertLastCount) {
                        updateList(data.Alerts.reversed())
                        alertLastCount = data.Alerts.size
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failure")
                println(e.message)
            }
        })
    }


    fun updateList(itemList: List<Alert>) {
        runOnUiThread{
            alerts_list?.adapter = ListAdapter(this, itemList)
            alerts_list?.layoutManager = LinearLayoutManager(this)
        }
    }
}

class HomeData(val S1: StationData, val S2: StationData, val Gas: GasData, val Alerts: List<Alert>)

class StationData(val Temp: Int, val Humid: Int)

class GasData(val PM25: Int)

class Alert(val Title: String, val Body: String, val Time: Long)

class ListAdapter(val context: Context?, val alerts: List<Alert>)
    : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bind(alerts[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_alerts, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = this.alerts.count()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val alerttitle = itemView.findViewById<TextView>(R.id.alert_title)
        //private val alertimage = itemView.findViewById<TextView>(R.id.alert_image)
        private val alerttime = itemView.findViewById<TextView>(R.id.alert_time)
        private val alertbody = itemView.findViewById<TextView>(R.id.alert_body)
        //private val alertcard = itemView.findViewById<TextView>(R.id.alert_card)

        fun bind(alert: Alert) {
            alerttitle.text = alert.Title
            alertbody.text = alert.Body

            if (alert.Time > 0) {
                val dt = Date(alert.Time*1000)
                val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm")
                alerttime.text = sdf.format(dt)
            }

        }
    }
}