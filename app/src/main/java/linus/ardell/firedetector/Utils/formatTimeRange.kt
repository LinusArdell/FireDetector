package linus.ardell.firedetector.Utils

import java.text.SimpleDateFormat
import java.util.Locale

fun formatTimeRange(startTime: String, endTime: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val start = inputFormat.parse(startTime)
        val end = inputFormat.parse(endTime)

        if (start != null && end != null) {
            val startFormatted = outputFormat.format(start)
            val endFormatted = outputFormat.format(end)
            "Fire detected at $startFormatted until $endFormatted"
        } else {
            "Invalid Time Range"
        }
    } catch (e: Exception) {
        "Invalid Time Range"
    }
}