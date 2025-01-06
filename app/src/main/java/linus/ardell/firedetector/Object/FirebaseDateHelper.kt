package linus.ardell.firedetector.Object

import android.util.Log
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale

object FirebaseDateHelper {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale("id", "ID"))

    /**
     * Function to fetch the current time from the "Main" tree in Firebase
     * and update the provided TextView with the formatted date.
     *
     * @param database Reference to the Firebase database tree ("Main").
     * @param textView TextView to update with the formatted date.
     */
    fun fetchAndUpdateDate(database: DatabaseReference, textView: TextView) {
        database.child("currentTime").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentTime = snapshot.getValue(String::class.java)
                if (currentTime != null) {
                    val formattedDate = formatDate(currentTime)
                    textView.text = "Last Ping : $formattedDate"
                } else {
                    textView.text = "Invalid Date"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDateHelper", "Error reading Firebase: ${error.message}")
                textView.text = "Error fetching date"
            }
        })
    }

    /**
     * Helper function to format the date from the input string.
     * Converts from "yyyy-MM-dd HH:mm:ss" to "dd MMMM yyyy, HH:mm:ss".
     *
     * @param currentTime The string representation of the date from Firebase.
     * @return A formatted date string, or "Invalid Date" if parsing fails.
     */
    private fun formatDate(currentTime: String): String {
        return try {
            val date = dateFormat.parse(currentTime)
            date?.let { outputDateFormat.format(it) } ?: "Invalid Date"
        } catch (e: Exception) {
            Log.e("FirebaseDateHelper", "Error formatting date: ${e.message}")
            "Invalid Date"
        }
    }
}
