package linus.ardell.firedetector.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import linus.ardell.firedetector.DataClass.HistoryData
import linus.ardell.firedetector.R
import linus.ardell.firedetector.Utils.calculateActiveDuration
import linus.ardell.firedetector.Utils.formatDate
import linus.ardell.firedetector.Utils.formatFireDetectedDay
import linus.ardell.firedetector.Utils.formatTimeRange

class HistoryAdapter(private val historyList: List<HistoryData>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFireDetectedDay: TextView = view.findViewById(R.id.tv_fire_detected_day)
        val tvHistoryDate: TextView = view.findViewById(R.id.tv_history_date)
        val tvHistoryDateRange: TextView = view.findViewById(R.id.tv_history_date_range)
        val tvHistorySprinkler: TextView = view.findViewById(R.id.tv_history_sprinkler)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]
        holder.tvFireDetectedDay.text = formatFireDetectedDay(history.currentTimeStart)
        holder.tvHistoryDate.text = formatDate(history.currentTimeStart)
        holder.tvHistoryDateRange.text = formatTimeRange(history.currentTimeStart, history.currentTimeEnd)

        // Tambahkan pumpStatus dari data history ke fungsi
        holder.tvHistorySprinkler.text = calculateActiveDuration(
            history.currentTimeStart,
            history.currentTimeEnd,
            history.pumpStatus
        )
    }

    override fun getItemCount(): Int = historyList.size
}