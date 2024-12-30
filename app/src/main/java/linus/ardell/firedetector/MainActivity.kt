package linus.ardell.firedetector

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import linus.ardell.firedetector.Adapter.ViewPagerAdapter
import linus.ardell.firedetector.Fragment.HomeFragment
import linus.ardell.firedetector.Fragment.MetricsFragment
import linus.ardell.firedetector.Fragment.ProfileFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkNotificationPermission()
        startFireAlarmService()

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val fragments = listOf(
            ProfileFragment(),
            HomeFragment(),
            MetricsFragment()
        )

        val adapterFragment = ViewPagerAdapter(this, fragments)
        viewPager.adapter = adapterFragment

        val initialPosition = 1
        viewPager.setCurrentItem(initialPosition, false)

        setupPageChangeIndicator(viewPager, initialPosition)

    }

    private fun startFireAlarmService() {
        val serviceIntent = Intent(this, FireAlarmService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Notification permission granted.")
            } else {
                Log.e(TAG, "Notification permission denied.")
            }
        }
    }

    private fun setupPageChangeIndicator(viewPager: ViewPager2, initialPosition: Int) {
        val indicators = listOf(
            findViewById<ImageView>(R.id.indicator_1),
            findViewById<ImageView>(R.id.indicator_2),
            findViewById<ImageView>(R.id.indicator_3)
        )

        fun updateIndicators(position: Int) {
            val indicatorColor = ContextCompat.getColor(this, R.color.indicator)
            val notSelectedColor = ContextCompat.getColor(this, R.color.grey)

            fun Int.dpToPx(): Int {
                return (this * Resources.getSystem().displayMetrics.density).toInt()
            }

            indicators.forEachIndexed { index, imageView ->
                imageView.setColorFilter(
                    if (index == position) indicatorColor else notSelectedColor
                )
            }
        }

        updateIndicators(initialPosition)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
            }
        })
    }
}