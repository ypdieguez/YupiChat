package com.sapp.yupi.services

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class SBNLService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.apply {
            val e = notification.extras
            val b = e.getString(Notification.EXTRA_TEXT)
            val a = e.getString(Notification.EXTRA_BIG_TEXT)
            val c = 1
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}