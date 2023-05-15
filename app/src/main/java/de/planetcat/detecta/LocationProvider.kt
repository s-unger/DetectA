package de.planetcat.detecta

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.location.component1
import androidx.core.location.component2
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY


class LocationProvider (val context: Context, val logger: Logger) {
    var location: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googlePlayAvailable = true

    init {
        Log.w("Context: ", context.toString())
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) != ConnectionResult.SUCCESS) {
            googlePlayAvailable = false
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        }
    }

    fun log() {
        if (googlePlayAvailable){
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.getCurrentLocation(PRIORITY_BALANCED_POWER_ACCURACY, null).addOnSuccessListener { currentLocation: Location? ->
                    if (isNewLocation(currentLocation)) {
                        location = currentLocation
                        logger.log("LO "+currentLocation.toString())
                    }
                }
            }
        }
    }

    private fun isNewLocation(currentLocation: Location?): Boolean {
        val location = this.location
        if (currentLocation != null) {
            return if (location != null) {
                !(currentLocation.component1() == location.component1() &&
                        currentLocation.component2() == location.component2() &&
                        currentLocation.altitude == location.altitude)
            } else {
                true
            }
        }
        return false
    }

}