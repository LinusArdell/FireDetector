package linus.ardell.firedetector.Utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(rawTime: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val date = inputFormat.parse(rawTime)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        "Invalid Date"
    }
}