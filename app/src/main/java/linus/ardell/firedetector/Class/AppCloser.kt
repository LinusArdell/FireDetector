package linus.ardell.firedetector.Class

import android.app.Activity

class AppCloser {
    fun closeApp(activity: Activity) {
        activity.finishAffinity() // Menutup semua aktivitas dalam aplikasi
        System.exit(0) // Mengakhiri proses aplikasi
    }
}