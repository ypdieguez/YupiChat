package com.sapp.yupi

import android.app.Application
import android.content.Intent
import com.sapp.yupi.services.IncomingSmsMmsService

class App : Application() {

    override fun onCreate() {
        super.onCreate()

//        startService(Intent(this, IncomingSmsMmsService::class.java))
    }
}