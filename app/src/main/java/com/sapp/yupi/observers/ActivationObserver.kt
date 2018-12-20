package com.sapp.yupi.observers

import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.provider.Telephony
import android.telephony.PhoneNumberUtils
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.sapp.yupi.workers.IncomingMsgWorker
import com.sapp.yupi.workers.KEY_BODY
import com.sapp.yupi.workers.KEY_DATE
import com.sapp.yupi.workers.KEY_MSG_ID

interface ActivationListener {
    fun success()
}

class ActivationObserver(
        contentResolver: ContentResolver,
        private val listener: ActivationListener
) : SmsObserver(contentResolver) {

    override fun handleMsg(id: Long, date: Long, body: String) {
        // Do the work
        if (body.contentEquals("Cuenta activada.")) {
            listener.success()
        }
    }
}
