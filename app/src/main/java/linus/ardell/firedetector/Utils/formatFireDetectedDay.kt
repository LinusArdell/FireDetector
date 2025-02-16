package linus.ardell.firedetector.Utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun formatFireDetectedDay(rawTime: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(rawTime)
        val calendar = Calendar.getInstance()
        val currentCalendar = Calendar.getInstance()

        date?.let {
            calendar.time = date

            // Set both calendars to midnight for accurate date-only comparison
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            currentCalendar.set(Calendar.HOUR_OF_DAY, 0)
            currentCalendar.set(Calendar.MINUTE, 0)
            currentCalendar.set(Calendar.SECOND, 0)
            currentCalendar.set(Calendar.MILLISECOND, 0)

            val diffInDays = ((currentCalendar.timeInMillis - calendar.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

            when (diffInDays) {
                0 -> "Fire Detected (Today)"
                1 -> "Fire Detected (Yesterday)"
                2 -> "Fire Detected (2 days ago)"
                else -> {
                    val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
                    "Fire Detected (${calendar.get(Calendar.DAY_OF_MONTH)} ${monthFormat.format(calendar.time)} ${calendar.get(Calendar.YEAR)})"
                }
            }
        } ?: "Fire Detected (Unknown)"
    } catch (e: Exception) {
        "Fire Detected (Unknown)"
    }
}
