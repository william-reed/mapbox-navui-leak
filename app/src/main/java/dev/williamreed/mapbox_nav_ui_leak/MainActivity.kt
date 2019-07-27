package dev.williamreed.mapbox_nav_ui_leak

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.mapboxsdk.Mapbox

class MainActivity : AppCompatActivity(), HomeFragment.HomeFragmentCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Mapbox.getInstance(applicationContext, "your key here")


        supportFragmentManager.beginTransaction().apply {
            add(R.id.container, HomeFragment())
            commit()
        }
    }

    // switch to nav fragment when pressed
    override fun onNavButtonPressed() {
        supportFragmentManager.beginTransaction().apply {
            val fragment = NavFragment()
            replace(R.id.container, fragment)
            addToBackStack(null)
            commit()
        }
    }

//    override fun onBackPressed() {
//        if (!supportFragmentManager.popBackStackImmediate()) {
//            super.onBackPressed()
//        }
//    }
}
