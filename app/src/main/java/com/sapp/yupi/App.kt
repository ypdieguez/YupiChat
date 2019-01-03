package com.sapp.yupi

import android.app.Application
import com.sapp.yupi.util.NetworkUtil
import com.sapp.yupi.util.UserPrefUtil

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        NetworkUtil.init(this)
        UserPrefUtil.init(this)
    }
}