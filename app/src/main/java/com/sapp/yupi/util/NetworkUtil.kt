package com.sapp.yupi.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Must be init in App.
 */
class NetworkUtil {
    companion object {
        private lateinit var cm: ConnectivityManager

        fun init(context: Context) {
            cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }

        fun isConnected() = cm.activeNetworkInfo.isConnected
    }
}