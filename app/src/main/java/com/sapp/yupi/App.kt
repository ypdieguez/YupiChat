package com.sapp.yupi

import android.app.Application
import com.sapp.yupi.utils.NetworkStatus

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkStatus.init(this)
    }
}