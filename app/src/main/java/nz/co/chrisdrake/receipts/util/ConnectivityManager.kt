package nz.co.chrisdrake.receipts.util

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun ConnectivityManager.isUnmeteredNetwork(): Boolean {
    return activeNetwork
        ?.let(::getNetworkCapabilities)
        ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        ?: false
}