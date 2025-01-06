package linus.ardell.firedetector.Class

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import linus.ardell.firedetector.R

class PumpController(private val view: View, private val database: DatabaseReference) {

    private var isPumpOn: Boolean = false
    private var isAutoMode: Boolean = false

    /**
     * Sets up the pump button behavior and listens for changes in "auto" mode
     * to enable or disable the button. Updates tv_sprinkler_status based on pump state.
     */
    fun setupPumpButton() {
        val button = view.findViewById<Button>(R.id.pump_status)
        val tvSprinklerStatus = view.findViewById<TextView>(R.id.tv_sprinkler_status)

        // Listen for changes in "auto" and "pumpStatus"
        database.child("auto").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val auto = snapshot.getValue(Int::class.java) ?: 0
                isAutoMode = auto == 0

                // Update button state based on "auto" value
                button.isEnabled = !isAutoMode
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PumpController", "Error reading auto value: ${error.message}")
            }
        })

        database.child("pumpStatus").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pumpStatus = snapshot.getValue(Int::class.java) ?: 1
                isPumpOn = pumpStatus == 0

                // Update the sprinkler status text based on pump status
                tvSprinklerStatus.text = if (isPumpOn) {
                    "Sprinkler is active"
                } else {
                    "Sprinkler is off"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PumpController", "Error reading pumpStatus: ${error.message}")
            }
        })

        // Set up button click listener
        button.setOnClickListener {
            if (!isAutoMode) {
                val newPumpStatus = if (isPumpOn) 1 else 0
                database.child("pumpStatus").setValue(newPumpStatus).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Toggle local pump state after successful update
                        button.text = if (isPumpOn) "Turn OFF" else "Turn ON"
                    } else {
                        //
                    }
                }
            }
        }
    }
}
