package linus.ardell.firedetector.Utils

import java.text.SimpleDateFormat
import java.util.Locale

private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

fun calculateActiveDuration(currentTimeStart: String, currentTimeEnd: String, pumpStatus: Int): String {
    return if (pumpStatus == 1) {
        // Jika pumpStatus bernilai 1
        "Sprinkler is disabled due to Manual override"
    } else {
        // Jika pumpStatus bernilai 0
        try {
            val startTime = dateFormat.parse(currentTimeStart)?.time ?: return "Invalid Time"
            val endTime = dateFormat.parse(currentTimeEnd)?.time ?: return "Invalid Time"

            val durationMillis = endTime - startTime
            val durationSeconds = durationMillis / 1000
            val durationMinutes = durationSeconds / 60
            val durationHours = durationMinutes / 60

            when {
                durationHours > 0 -> "Sprinkler Active for $durationHours Hours"
                durationMinutes > 0 -> "Sprinkler Active for $durationMinutes Minutes"
                else -> "Sprinkler Active for $durationSeconds Seconds"
            }
        } catch (e: Exception) {
            "Invalid Time Format"
        }
    }
}
