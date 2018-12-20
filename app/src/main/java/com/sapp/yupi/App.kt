package com.sapp.yupi

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sapp.yupi.ui.appintro.TextInputFragment
import com.sapp.yupi.ui.appintro.USER_PREFERENCES
import com.sapp.yupi.util.NetworkUtil
import com.sapp.yupi.util.UserPrefUtil

class App : Application() {

    lateinit var pref: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        NetworkUtil.init(this)
        UserPrefUtil.init(this)
    }

    fun getInst() = this

    fun getUserPref() = pref

    fun getStatus(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo.isConnected
    }
}