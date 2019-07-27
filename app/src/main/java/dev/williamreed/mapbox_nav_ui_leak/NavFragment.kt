package dev.williamreed.mapbox_nav_ui_leak

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.services.android.navigation.ui.v5.NavigationView
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// adapted from https://docs.mapbox.com/android/navigation/examples/navigationview-with-fragment/
class NavFragment : Fragment(), OnNavigationReadyCallback, NavigationListener, ProgressChangeListener {
    companion object {
        const val ORIGIN_LONG = -3.714873
        const val ORIGIN_LAT = 40.397389

        const val DEST_LONG = -3.712331
        const val DEST_LAT = 40.401686
    }

    private var navigationView: NavigationView? = null
    private var directionsRoute: DirectionsRoute? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationView = view.findViewById<NavigationView>(R.id.nav_view).apply {
            onCreate(savedInstanceState)
            initialize(this@NavFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        navigationView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        navigationView?.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigationView?.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            navigationView?.onRestoreInstanceState(savedInstanceState)
        }
    }

    override fun onPause() {
        super.onPause()
        navigationView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        navigationView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        navigationView?.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        navigationView?.onDestroy()
    }

    override fun onNavigationReady(isRunning: Boolean) {
        val origin = Point.fromLngLat(ORIGIN_LONG, ORIGIN_LAT)
        val destination = Point.fromLngLat(DEST_LONG, DEST_LAT)
        fetchRoute(origin, destination)
    }

    override fun onCancelNavigation() {
        navigationView?.stopNavigation()
        fragmentManager?.popBackStack()
    }

    override fun onNavigationFinished() {
        // no-op
    }

    override fun onNavigationRunning() {
        // no-op
    }

    override fun onProgressChange(location: Location, routeProgress: RouteProgress) {
        // no-op
    }

    private fun fetchRoute(origin: Point, destination: Point) {
        NavigationRoute.builder(context)
            .accessToken(Mapbox.getAccessToken()!!)
            .origin(origin)
            .destination(destination)
            .build()
            .getRoute(object : Callback<DirectionsResponse> {
                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    // F
                }

                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    directionsRoute = response.body()?.routes()?.get(0)
                    startNavigation()
                }
            })
    }

    private fun startNavigation() {
        if (directionsRoute == null) {
            return
        }
        val options = NavigationViewOptions.builder()
            .directionsRoute(directionsRoute)
            .shouldSimulateRoute(true)
            .navigationListener(this)
            .progressChangeListener(this)
            .build()
        navigationView?.startNavigation(options)
    }
}
