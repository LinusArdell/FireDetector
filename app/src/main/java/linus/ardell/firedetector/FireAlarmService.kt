package linus.ardell.firedetector

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import java.security.Provider
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FireAlarmService : Service() {
    companion object {
        private const val CHANNEL_ID = "fire_alarm_channel"
        private const val NOTIFICATION_ID = 1
    }

    private lateinit var mainRef: DatabaseReference

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Fire Alarm Service",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi untuk peringatan kebakaran"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)

            val notification = Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Fire Alarm Active")
                .setContentText("Monitoring fire alarms.")
                .setSmallIcon(R.drawable.ic_fire)
                .build()

            startForeground(NOTIFICATION_ID, notification)
        }

        startForeground(NOTIFICATION_ID, createNotification("Fire Alarm Service Active"))

        mainRef = FirebaseDatabase.getInstance().getReference("Main")
        mainRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val sensorStatus = snapshot.child("sensorStatus").getValue(Int::class.java)
                    if (sensorStatus == 0) {
                        showNotification("Peringatan Kebakaran!", "Api terdeteksi oleh sensor!")
                        triggerVibration()
                    } else {
                        showNotification("Fire Alarm Service", "Fire Alarm Service is Active")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun showNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_fire) // Ganti dengan ikon Anda
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(contentText: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Fire Alarm Service")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_fire)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun triggerVibration() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            val pattern = longArrayOf(0, 1000, 500, 1000)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1)) // -1 = no repeat
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.cancel()
    }
}