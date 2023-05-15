package de.planetcat.detecta

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager


class NetworkProvider (private val context: Context, val logger: Logger) {

    data class NetworkInformation(var networkCellular: String, var networkWifi: String)
    var networkInformation = NetworkInformation("UNKNOWN", "UNKNOWN")

    fun log() {
        val currentNetwork = getNetworkStrings()
        if (networkInformation.networkWifi != currentNetwork.networkWifi ||
            networkInformation.networkCellular != currentNetwork.networkCellular) {
            logger.log("NW ${currentNetwork.networkCellular} ${currentNetwork.networkWifi}")
            networkInformation.networkCellular = currentNetwork.networkCellular
            networkInformation.networkWifi = currentNetwork.networkWifi
        }
    }

    private fun getNetworkStrings(): NetworkInformation {
        val networkStrings = NetworkInformation("DISCONNECTED", "DISCONNECTED")
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork ?: return networkStrings
        val networkCapabilities = connectivityManager.getNetworkCapabilities(currentNetwork) ?: return networkStrings

        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            networkStrings.networkCellular = "CELLULAR"
        }

        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            @Suppress("DEPRECATION") val wifiInfo = wifiManager.connectionInfo
            if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
                networkStrings.networkWifi = wifiInfo.ssid
            } else {
                networkStrings.networkWifi = "CONNECTING"
            }
        }
        return networkStrings
    }
}