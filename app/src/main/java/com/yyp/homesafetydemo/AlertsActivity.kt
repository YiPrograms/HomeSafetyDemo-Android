package com.yyp.homesafetydemo

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_alerts.*


class AlertsActivity : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_alerts, container, false)
    }

    fun UpdateList() {
        val itemList: MutableList<alert_item> = mutableListOf()

        itemList.add(alert_item("Fire", "2019/8/1 12:00"))
        itemList.add(alert_item("Smoke", "2019/8/1 12:00"))
        itemList.add(alert_item("Humid", "2019/8/1 12:00"))

        activity?.runOnUiThread{
            alerts_list?.adapter = ListAdapter(this.context, itemList)
            alerts_list?.layoutManager = LinearLayoutManager(this.context)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        UpdateList()
    }

}

class ListAdapter(val context: Context?, val alerts: List<alert_item>)
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
        //private val alertcard = itemView.findViewById<TextView>(R.id.alert_card)

        fun bind(alert: alert_item) {
            alerttitle.text = alert.type
            alerttime.text = alert.time

        }
    }
}

class AlertsListAdapter(val context: Context?, val alerts: List<alert_item>) : RecyclerView.Adapter<AlertsListAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return alerts.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(alerts[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alerts, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val alert_title = itemView.findViewById<TextView>(R.id.alert_title)
        private val alert_image = itemView.findViewById<TextView>(R.id.alert_image)
        private val alert_time = itemView.findViewById<TextView>(R.id.alert_time)
        private val alert_card = itemView.findViewById<TextView>(R.id.alert_card)

        fun bind(alert: alert_item) {
            alert_title.text = alert.type
            alert_time.text = alert.time

        }
    }
}

class alert_item(
    val type: String,
    val time: String
)