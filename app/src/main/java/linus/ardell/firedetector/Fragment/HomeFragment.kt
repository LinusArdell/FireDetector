package linus.ardell.firedetector.Fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import linus.ardell.firedetector.Class.AutoModeController
import linus.ardell.firedetector.Class.ESP32StatusUpdater
import linus.ardell.firedetector.Class.Esp32StatusController
import linus.ardell.firedetector.Class.PumpController
import linus.ardell.firedetector.Object.FirebaseDateHelper
import linus.ardell.firedetector.Object.FirebaseHelper
import linus.ardell.firedetector.R
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var databaseMain: DatabaseReference
    private lateinit var databaseHistory: DatabaseReference
    private lateinit var autoModeController: AutoModeController
    private lateinit var pumpController: PumpController
    private lateinit var esp32StatusUpdater: ESP32StatusUpdater
    private lateinit var tvDataTanggal: TextView

    private val handler = Handler(Looper.getMainLooper())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDataTanggal = view.findViewById(R.id.tv_data_tanggal)

        // Firebase references
        databaseMain = FirebaseDatabase.getInstance().getReference("Main")
        databaseHistory = FirebaseDatabase.getInstance().getReference("History")

        // Initialize controllers
        autoModeController = AutoModeController(requireView(), databaseMain)
        pumpController = PumpController(requireView(), databaseMain)
        esp32StatusUpdater = ESP32StatusUpdater(requireView(), handler)

        FirebaseDateHelper.fetchAndUpdateDate(databaseMain, tvDataTanggal)

        // Fetch and display today's stats
        FirebaseHelper.fetchTodayStats(databaseHistory) { fireCount, waterCount ->
            requireView().findViewById<TextView>(R.id.tv_total_fire_today).text = "${fireCount.toString()} Fire detected today"
            requireView().findViewById<TextView>(R.id.tv_total_water_todday).text = "${waterCount.toString()} Sprinkler used Today"
        }

        val esp32StatusController = Esp32StatusController(view, databaseMain)
        esp32StatusController.startMonitoring()

        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                esp32StatusController.stopMonitoring()
            }
        })

        // Observe Firebase updates
        autoModeController.observeFirebaseData()
        esp32StatusUpdater.startUpdating()

        // Setup button listeners
        autoModeController.setupModeButton()
        pumpController.setupPumpButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        esp32StatusUpdater.stopUpdating()
    }
}
