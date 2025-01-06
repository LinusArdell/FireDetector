package linus.ardell.firedetector.Class

import android.graphics.Color
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import linus.ardell.firedetector.R
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.logging.Handler

class Esp32StatusController(
    private val view: View,
    private val database: DatabaseReference
) {

    private val esp32Status: TextView = view.findViewById(R.id.esp32_status)
    private val tvSubtitle: TextView = view.findViewById(R.id.tv_subtitle)
    private val ivTitleIcon: ImageView = view.findViewById(R.id.iv_title_icon)
    private val progressOnline: ProgressBar = view.findViewById(R.id.progress_online)

    private val handler = android.os.Handler(Looper.getMainLooper())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private var lastUpdateTime: Long = 0L

    /**
     * Initializes the ESP32 status updater.
     */
    fun startMonitoring() {
        // Listen for changes to currentTime in the database
        database.child("currentTime").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentTime = snapshot.getValue(String::class.java)
                currentTime?.let {
                    updateLastUpdateTime(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Esp32StatusController", "Error reading currentTime: ${error.message}")
            }
        })

        // Periodically check the status
        handler.postDelayed(updateStatusRunnable, 1000)
    }

    /**
     * Stops the monitoring to prevent memory leaks.
     */
    fun stopMonitoring() {
        handler.removeCallbacks(updateStatusRunnable)
    }

    /**
     * Updates the last update time from Firebase.
     */
    private fun updateLastUpdateTime(currentTime: String) {
        try {
            val parsedTime = dateFormat.parse(currentTime)?.time
            lastUpdateTime = parsedTime ?: 0L
        } catch (e: Exception) {
            Log.e("Esp32StatusController", "Error parsing currentTime: ${e.message}")
            lastUpdateTime = 0L
        }
    }

    /**
     * Periodically checks if the ESP32 is online or offline.
     */
    private val updateStatusRunnable = object : Runnable {
        override fun run() {
            val currentTimeMillis = System.currentTimeMillis()

            if (lastUpdateTime != 0L && currentTimeMillis - lastUpdateTime <= 2000) {
                // ESP32 is Online
                esp32Status.text = "Online"
                esp32Status.setTextColor(Color.GREEN)
                tvSubtitle.text = "ESP32 is Online"
                ivTitleIcon.visibility = View.GONE
                progressOnline.visibility = View.VISIBLE
            } else {
                // ESP32 is Offline
                esp32Status.text = "Offline"
                esp32Status.setTextColor(Color.RED)
                tvSubtitle.text = "ESP32 is Offline"
                ivTitleIcon.visibility = View.VISIBLE
                progressOnline.visibility = View.GONE
            }

            handler.postDelayed(this, 1000)
        }
    }
}