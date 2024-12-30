package linus.ardell.firedetector.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import linus.ardell.firedetector.Adapter.HistoryAdapter
import linus.ardell.firedetector.DataClass.HistoryData
import linus.ardell.firedetector.R

class MetricsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private val historyList = mutableListOf<HistoryData>()

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_metrics, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_history)

        historyAdapter = HistoryAdapter(historyList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = historyAdapter

        database = FirebaseDatabase.getInstance().getReference("History")

        fetchHistoryData()

        return view
    }

    private fun fetchHistoryData() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                historyList.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(HistoryData::class.java)
                    item?.let { historyList.add(it) }
                }
                historyAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("StatisticFragment", "Error fetching data: ${error.message}")
            }
        })
    }
}