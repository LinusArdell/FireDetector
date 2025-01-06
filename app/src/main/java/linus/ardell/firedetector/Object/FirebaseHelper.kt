package linus.ardell.firedetector.Object

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FirebaseHelper {

    fun fetchTodayStats(database: DatabaseReference, callback: (fireCount: Int, waterCount: Int) -> Unit) {
        val todayDate = getTodayDate()

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var fireCount = 0
                var waterCount = 0

                for (data in snapshot.children) {
                    val currentTimeStart = data.child("currentTimeStart").value.toString()
                    val sensorStatus = data.child("sensorStatus").value.toString().toIntOrNull()
                    val pumpStatus = data.child("pumpStatus").value.toString().toIntOrNull()

                    if (currentTimeStart.startsWith(todayDate)) {
                        if (sensorStatus == 0) {
                            fireCount++
                        }
                        if (pumpStatus == 0) {
                            waterCount++
                        }
                    }
                }

                callback(fireCount, waterCount)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read data", error.toException())
                callback(0, 0)
            }
        })
    }

    private fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}