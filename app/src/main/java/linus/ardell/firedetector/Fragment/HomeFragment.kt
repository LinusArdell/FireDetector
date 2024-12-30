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
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import linus.ardell.firedetector.Adapter.DataAdapter
import linus.ardell.firedetector.DataClass.MainData
import linus.ardell.firedetector.R
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var buttonMode: Button
    private lateinit var buttonPump: Button
    private lateinit var buttonSensor: Button
    private lateinit var buttonDate: Button
    private lateinit var textESP32Status: TextView
    private var isAutoMode: Boolean = false
    private var isPumpOn: Boolean = false

    private val handler = Handler(Looper.getMainLooper())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private var lastFirebaseTime: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi View
        buttonMode = view.findViewById(R.id.button_mode)
        buttonPump = view.findViewById(R.id.pump_status)
        buttonSensor = view.findViewById(R.id.sensor_status)
        buttonDate = view.findViewById(R.id.button_date)
        textESP32Status = view.findViewById(R.id.esp32_status)

        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance().getReference("Main")
        observeFirebaseData()

        // Klik tombol Mode
        buttonMode.setOnClickListener {
            toggleAutoMode()
        }

        // Klik tombol Pump
        buttonPump.setOnClickListener {
            togglePump()
        }

        // Memulai pembaruan status ESP32
        handler.postDelayed(updateStatusRunnable, 1000)

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

    private fun updateUI(auto: Int, pumpStatus: Int, sensorStatus: Int, currentTime: String) {
        // Update tombol mode
        isAutoMode = auto == 1
        buttonMode.text = if (isAutoMode) "Mode: Auto" else "Mode: Manual"

        // Update tombol Pump
        buttonPump.isEnabled = !isAutoMode // Disable tombol jika mode Auto
        isPumpOn = pumpStatus == 1
        buttonPump.text = if (isPumpOn) "Pump: ON" else "Pump: OFF"

        // Update tombol lainnya
        buttonSensor.text = "Sensor: $sensorStatus"
        buttonDate.text = "Time: $currentTime"
    }

    private fun toggleAutoMode() {
        isAutoMode = !isAutoMode
        val newValue = if (isAutoMode) 1 else 0
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

        val newPumpStatus = if (isPumpOn) 0 else 1
        database.child("pumpStatus").setValue(newPumpStatus).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("HomeFragment", "Pompa berhasil diubah ke status ${if (newPumpStatus == 1) "ON" else "OFF"}")
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
            textESP32Status.text = "ESP32 Status: $status"
            handler.postDelayed(this, 2000)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateStatusRunnable)
    }
}

