package com.sapp.yupi.receivers

import android.content.Context
import android.content.Intent
import androidx.legacy.content.WakefulBroadcastReceiver
import com.sapp.yupi.services.NotificationService

class NotificationReceiver : WakefulBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        context.startService(
                Intent(context, NotificationService::class.java).apply {
                    putExtras(intent.extras!!)
                }
        )
    }
}
