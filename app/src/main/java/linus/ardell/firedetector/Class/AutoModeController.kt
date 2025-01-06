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

class AutoModeController(private val view: View, private val database: DatabaseReference) {

    private var isAutoMode: Boolean = false
    private var isPumpActive: Boolean = false

    fun observeFirebaseData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val auto = snapshot.child("auto").getValue(Int::class.java) ?: 0
                val sprinkler = snapshot.child("pumpStatus").getValue(Int::class.java) ?: 0
                isAutoMode = auto == 0
                isPumpActive = sprinkler == 0

                updateUI()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AutoModeController", "Failed to read data: ${error.message}")
            }
        })
    }

    fun setupModeButton() {
        val button = view.findViewById<Button>(R.id.button_mode)
        button.setOnClickListener {
            isAutoMode = !isAutoMode
            database.child("auto").setValue(if (isAutoMode) 0 else 1)
        }
    }

    private fun updateUI() {
        val button = view.findViewById<Button>(R.id.button_mode)
        val buttonSprinkler = view.findViewById<Button>(R.id.pump_status)
        val textView = view.findViewById<TextView>(R.id.tv_system_mode)
        button.text = if (isAutoMode) "Auto" else "Manual"
        buttonSprinkler.text = if (isPumpActive) "Turn Off" else "Turn On"
        textView.text = "Current Mode: ${if (isAutoMode) "Auto" else "Manual"}"
    }
}
