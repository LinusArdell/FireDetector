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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import linus.ardell.firedetector.Object.FirebaseHelper
import linus.ardell.firedetector.R
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var ivTitleIcons: ImageView
    private lateinit var progressOnlines: ProgressBar
    private lateinit var tvSubtitles: TextView
    private lateinit var esp32Statuss: TextView

    private lateinit var tvSystemMode : TextView
    private lateinit var tvSprinklerStatus : TextView

    private lateinit var database: DatabaseReference
    private lateinit var buttonMode: Button
    private lateinit var buttonPump: Button
    private lateinit var buttonSensor: Button
    private lateinit var textESP32Status: TextView
    private lateinit var tvDataTanggal: TextView
    private var isAutoMode: Boolean = false
    private var isPumpOn: Boolean = false

    private lateinit var tvTotalFireToday: TextView
    private lateinit var tvTotalWaterTodday : TextView

    private val handler = Handler(Looper.getMainLooper())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputDateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale("id", "ID"))
    private var lastFirebaseTime: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        buttonMode = view.findViewById(R.id.button_mode)
        buttonPump = view.findViewById(R.id.pump_status)
        buttonSensor = view.findViewById(R.id.sensor_status)
        textESP32Status = view.findViewById(R.id.esp32_status)
        tvDataTanggal = view.findViewById(R.id.tv_data_tanggal)

        ivTitleIcons = view.findViewById(R.id.iv_title_icon)
        progressOnlines = view.findViewById(R.id.progress_online)
        tvSubtitles = view.findViewById(R.id.tv_subtitle)
        esp32Statuss = view.findViewById(R.id.esp32_status)

        tvSystemMode = view.findViewById(R.id.tv_system_mode)
        tvSprinklerStatus = view.findViewById(R.id.tv_sprinkler_status)

        tvTotalFireToday = view.findViewById(R.id.tv_total_fire_today)
        tvTotalWaterTodday = view.findViewById(R.id.tv_total_water_todday)

        database = FirebaseDatabase.getInstance().getReference("Main")
        database = FirebaseDatabase.getInstance().getReference("History")
        observeFirebaseData()

        FirebaseHelper.fetchTodayStats(database) { fireCount, waterCount ->
            tvTotalFireToday.text = fireCount.toString()
            tvTotalWaterTodday.text = waterCount.toString()
        }

        buttonMode.setOnClickListener {
            toggleAutoMode()
        }

        buttonPump.setOnClickListener {
            togglePump()
        }

        handler.postDelayed(updateStatusRunnable, 1000)
        handler.postDelayed(updateStatusRunnables, 1000)

        return view
    }

    private fun observeFirebaseData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val auto = snapshot.child("auto").getValue(Int::class.java) ?: 0
                val pumpStatus = snapshot.child("pumpStatus").getValue(Int::class.java) ?: 0
                val sensorStatus = snapshot.child("sensorStatus").getValue(Int::class.java) ?: 0
                val currentTime = snapshot.child("currentTime").getValue(String::class.java) ?: "N/A"

                updateUI(auto, pumpStatus, sensorStatus, currentTime)
                updateLastFirebaseTime(currentTime)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Error reading Firebase: ${error.message}")
            }
        })
    }

    private fun formatDate(currentTime: String): String {
        return try {
            val date = dateFormat.parse(currentTime)
            date?.let { outputDateFormat.format(it) } ?: "Invalid Date"
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

    private fun updateUI(auto: Int, pumpStatus: Int, sensorStatus: Int, currentTime: String) {
        isAutoMode = auto == 0
        buttonMode.text = if (isAutoMode) "Auto" else "Manual"
        tvSystemMode.text = if (isAutoMode) "Current Mode : Auto" else "Current Mode : Manual"

        buttonPump.isEnabled = !isAutoMode
        isPumpOn = pumpStatus == 0
        buttonPump.text = if (isPumpOn) "ON" else "OFF"
        tvSprinklerStatus.text = if (isPumpOn) "Sprinkler is Active" else "Sprinkler is Off"

        buttonSensor.text = "Sensor: $sensorStatus"
        tvDataTanggal.text = "Last Ping : ${formatDate(currentTime)}"
    }

    private fun toggleAutoMode() {
        isAutoMode = !isAutoMode
        val newValue = if (isAutoMode) 0 else 1
        database.child("auto").setValue(newValue).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("HomeFragment", "Mode berhasil diubah")
            } else {
                Log.e("HomeFragment", "Gagal mengubah mode: ${task.exception?.message}")
            }
        }
    }

    private fun togglePump() {
        if (isAutoMode) {
            Log.w("HomeFragment", "Tidak dapat mengontrol pompa dalam mode Auto")
            return
        }

        val newPumpStatus = if (isPumpOn) 1 else 0
        database.child("pumpStatus").setValue(newPumpStatus).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("HomeFragment", "Pompa berhasil diubah ke status ${if (newPumpStatus == 0) "ON" else "OFF"}")
            } else {
                Log.e("HomeFragment", "Gagal mengubah status pompa: ${task.exception?.message}")
            }
        }
    }

    private fun updateLastFirebaseTime(currentTime: String) {
        try {
            val firebaseTime = dateFormat.parse(currentTime)
            lastFirebaseTime = firebaseTime?.time ?: 0L
        } catch (e: Exception) {
            lastFirebaseTime = 0L
        }
    }

    private val updateStatusRunnable = object : Runnable {
        override fun run() {
            val currentTimeMillis = System.currentTimeMillis()
            val status = if (lastFirebaseTime != 0L && lastFirebaseTime + 2000 >= currentTimeMillis) {
                textESP32Status.setTextColor(Color.GREEN)
                "Online"
            } else {
                textESP32Status.setTextColor(Color.RED)
                "Offline"
            }
            textESP32Status.text = "$status"
            handler.postDelayed(this, 2000)
        }
    }

    private val updateStatusRunnables = object : Runnable {
        override fun run() {
            val currentTimeMillis = System.currentTimeMillis()
            if (lastFirebaseTime != 0L && lastFirebaseTime + 2000 >= currentTimeMillis) {
                ivTitleIcons.visibility = View.GONE
                progressOnlines.visibility = View.VISIBLE
                tvSubtitles.text = getString(R.string.tv_subtitle)
            } else {
                ivTitleIcons.visibility = View.VISIBLE
                progressOnlines.visibility = View.GONE
                tvSubtitles.text = "ESP32 is OFFLINE"
            }
            handler.postDelayed(this, 2000)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateStatusRunnable)
    }
}