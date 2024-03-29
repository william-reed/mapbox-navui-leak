package dev.williamreed.mapbox_nav_ui_leak

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.openNavButton).setOnClickListener {
            (activity as MainActivity).onNavButtonPressed()
        }
        viewLifecycleOwner
    }

    interface HomeFragmentCallbacks {
        fun onNavButtonPressed()
    }
}
