package com.sapp.yupi.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.provider.Telephony.Mms
import android.provider.Telephony.Sms
import com.sapp.yupi.observers.MmsObserver
import com.sapp.yupi.observers.SmsObserver
import java.util.*


class IncomingSmsMmsService : Service() {

    private var initialized: Boolean = false
    private lateinit var smsObserver: SmsObserver
    private lateinit var mmsObserver: MmsObserver

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!initialized) {
            initializeService()
        }

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        restartService()
    }

    override fun onDestroy() {
        super.onDestroy()
        restartService()
    }

    private fun initializeService() {
        initialized = true
        // Initialize Observers
        smsObserver = SmsObserver(contentResolver)
        mmsObserver = MmsObserver(contentResolver)
        // Register Sms ContentObserver
        contentResolver.registerContentObserver(Sms.CONTENT_URI, true, smsObserver)
        contentResolver.registerContentObserver(Mms.CONTENT_URI, true, mmsObserver)
    }

    private fun restartService() {
        initialized = false

        // Unregister Observers
        contentResolver.unregisterContentObserver(smsObserver)
        contentResolver.unregisterContentObserver(mmsObserver)

        // Restart service
        val intent = Intent(this, IncomingSmsMmsService::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, intent, 0)
        (getSystemService(Context.ALARM_SERVICE) as AlarmManager).set(AlarmManager.RTC_WAKEUP,
                Date().time + 1000, pendingIntent)
    }
}
