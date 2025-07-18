package com.zonedev.minapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkUtils {

    fun isNetworkAvailable(context: Context): Boolean {
        // Obtiene el servicio de conectividad del sistema.
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Obtiene la red activa. Si no hay, no hay conexión.
        val network = connectivityManager.activeNetwork ?: return false

        // Obtiene las capacidades de la red activa. Si no tiene, no hay conexión.
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        // Verifica si la conexión es a través de Wi-Fi, datos móviles o Ethernet.
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}