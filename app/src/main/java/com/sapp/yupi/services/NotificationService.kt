package com.sapp.yupi.services

import android.app.IntentService
import android.content.Intent
import com.sapp.yupi.NOTIFICATION
import com.sapp.yupi.data.Notification
import com.sapp.yupi.utils.MessageNotification


class NotificationService : IntentService("NotificationService") {

    override fun onHandleIntent(intent: Intent) {
        val notification = intent.getParcelableExtra<Notification>(NOTIFICATION)
        notification.apply {
            MessageNotification.notify(this@NotificationService, message, contact)
        }
    }
}
