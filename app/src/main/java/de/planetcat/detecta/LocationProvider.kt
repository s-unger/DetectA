package de.planetcat.detecta

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY


class LocationProvider (val context: Context) {
    var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var googlePlayNotAvailable = false

    init {
        Log.w("Context: ", context.toString())
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) != ConnectionResult.SUCCESS) {
            googlePlayNotAvailable = true
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        }
    }

    fun getLocation(): Location? {
        if (googlePlayNotAvailable){
            return null
        } else {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                TODO("Handle Location not available.")
                //INTENT => MainActivity => Notification auslösen
            }
            fusedLocationClient.getCurrentLocation(PRIORITY_BALANCED_POWER_ACCURACY, null).addOnSuccessListener { location: Location? ->
                currentLocation = location
            }
            return currentLocation
        }
    }

}