package linus.ardell.firedetector.Class

import android.graphics.Color
import android.os.Handler
import android.view.SurfaceControl
import android.view.View
import android.widget.TextView
import com.google.firebase.database.Transaction
import linus.ardell.firedetector.R
import java.text.SimpleDateFormat
import java.util.Locale

class ESP32StatusUpdater(private val view: View, private val handler: Handler) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private var lastFirebaseTime: Long = 0L

    private val updateRunnable = object : Runnable {
        override fun run() {
            val currentTimeMillis = System.currentTimeMillis()
            val textView = view.findViewById<TextView>(R.id.esp32_status)
            val isOnline = lastFirebaseTime + 2000 >= currentTimeMillis
            textView.text = if (isOnline) "Online" else "Offline"
            textView.setTextColor(if (isOnline) Color.GREEN else Color.RED)
            handler.postDelayed(this, 2000)
        }
    }

    fun startUpdating() {
        handler.postDelayed(updateRunnable, 2000)
    }

    fun stopUpdating() {
        handler.removeCallbacks(updateRunnable)
    }
}
