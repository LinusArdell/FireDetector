package linus.ardell.firedetector.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import linus.ardell.firedetector.DataClass.HistoryData
import linus.ardell.firedetector.R

class HistoryAdapter(private val historyList: List<HistoryData>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTimeRange: TextView = itemView.findViewById(R.id.tv_time_range)
        val tvPumpStatus: TextView = itemView.findViewById(R.id.tv_pump_status)
        val tvSensorStatus: TextView = itemView.findViewById(R.id.tv_sensor_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.tvTimeRange.text = "Time Range: ${item.currentTimeStart} - ${item.currentTimeEnd}"
        holder.tvPumpStatus.text = "Pump Status: ${item.pumpStatus}"
        holder.tvSensorStatus.text = "Sensor Status: ${item.sensorStatus}"
    }

    override fun getItemCount(): Int = historyList.size
}
