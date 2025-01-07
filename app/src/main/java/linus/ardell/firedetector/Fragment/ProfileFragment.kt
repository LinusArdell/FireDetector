package linus.ardell.firedetector.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import linus.ardell.firedetector.Class.AppCloser
import linus.ardell.firedetector.R

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var buttonCloseApp: RelativeLayout
    private val appCloser = AppCloser()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonCloseApp = view.findViewById(R.id.rv_close_app)

        buttonCloseApp.setOnClickListener { appCloser.closeApp(requireActivity()) }
    }
}