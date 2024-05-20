package com.example.landmarkremark.ui.map


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log

class LocationChangedProvider : BroadcastReceiver() {

    private var isGpsEnabled: Boolean = false
    private var isNetworkEnabled: Boolean = false

    override fun onReceive(context: Context?, intent: Intent?) {

        intent?.let {
            if (it.action == Intent.ACTION_LOCALE_CHANGED) {
                val locationManager =
                    context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                isGpsEnabled =
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                isNetworkEnabled =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                Log.i(
                    TAG,
                    "Location Providers changed, is GPS Enabled: " + isGpsEnabled
                )

//                Start your Activity if location was enabled:
                if (isGpsEnabled || isNetworkEnabled) {
                    val i = Intent(context, MapFragment::class.java)
                    context.startActivity(i)
                }


            }
        }
    }

    companion object {
        private const val TAG = "LocationProviderChanged"
    }
}