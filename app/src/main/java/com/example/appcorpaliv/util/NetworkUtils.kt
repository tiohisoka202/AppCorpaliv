package com.example.appcorpaliv.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkUtils {
    /**
     * Recurso Nativo 2: Consulta el hardware/estado de red del dispositivo
     */
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}