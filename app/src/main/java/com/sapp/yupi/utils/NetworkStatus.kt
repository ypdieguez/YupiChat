package com.sapp.yupi.utils

import android.content.Context
import android.net.ConnectivityManager

/**
 * Must be init in App.
 */
class NetworkStatus {
    companion object {
        private lateinit var cm: ConnectivityManager

        fun init(context: Context) {
            cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }

        fun isConnected() = cm.activeNetworkInfo?.isConnected == true
    }
}