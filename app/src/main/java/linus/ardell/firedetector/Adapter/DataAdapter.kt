package linus.ardell.firedetector.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import linus.ardell.firedetector.DataClass.MainData
import linus.ardell.firedetector.R

class DataAdapter (private val dataList: List<MainData>) : RecyclerView.Adapter<MainViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_home, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val data = dataList[position]

        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}

class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val btnAuto: Button = itemView.findViewById(R.id.button_mode)
    val btnPumpStatus: Button = itemView.findViewById(R.id.pump_status)
    val btnSensorStatus: Button = itemView.findViewById(R.id.sensor_status)
    val tvDataTanggal : TextView = itemView.findViewById(R.id.tv_data_tanggal)

    fun bind(data: MainData) {
        btnAuto.text = "Auto: ${data.autoMode}"
        btnPumpStatus.text = "Pump: ${data.pumpStatus}"
        btnSensorStatus.text = "Sensor: ${data.sensorStatus}"
        tvDataTanggal.text = "Last Ping : ${data.dateTime}"
    }
}