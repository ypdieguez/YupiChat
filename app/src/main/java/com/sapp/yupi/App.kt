package com.sapp.yupi

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.sapp.yupi.util.MailSelector
import com.sapp.yupi.util.NetworkUtil
import com.sapp.yupi.util.UserPrefUtil

class App : Application() {

    lateinit var pref: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        NetworkUtil.init(this)
        UserPrefUtil.init(this)
        Email.init(this)
//        MailSelector.init(this)
    }

    fun getInst() = this

    fun getUserPref() = pref

    fun getStatus(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo.isConnected
    }
}