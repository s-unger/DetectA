package de.planetcat.detecta

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager


class NetworkProvider (val context: Context) {

    fun getNetworkName(): String {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork ?: return "DISCONNECTED"
        val networkCapabilities = connectivityManager.getNetworkCapabilities(currentNetwork) ?: return "DISCONNECTED"
        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            return "CELLULAR"
        } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                return wifiInfo.getSSID();
            } else {
                return "WIFI"
            }
        } else {
            return "OTHER"
        }
    }
}