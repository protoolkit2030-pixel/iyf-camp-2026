package com.iyf.camp2026.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object NetworkUtils {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            networkInfo?.isConnected == true
        }
    }

    fun getNetworkErrorMessage(exception: Exception): String {
        return when {
            exception.message?.contains("timeout", ignoreCase = true) == true ->
                "La connexion a expiré. Veuillez réessayer."
            exception.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                "Pas de connexion internet. Les données seront synchronisées ultérieurement."
            exception.message?.contains("Connection refused", ignoreCase = true) == true ->
                "Connexion refusée. Veuillez vérifier votre réseau."
            else -> "Erreur réseau: ${exception.localizedMessage ?: "Erreur inconnue"}"
        }
    }
}
