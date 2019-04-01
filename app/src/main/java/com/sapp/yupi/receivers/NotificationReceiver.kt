package com.sapp.yupi.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sapp.yupi.services.NotificationService

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        context.startService(
                Intent(context, NotificationService::class.java).apply {
                    putExtras(intent.extras!!)
                }
        )

        // Unregister
//        context.unregisterReceiver(this)
    }
}
