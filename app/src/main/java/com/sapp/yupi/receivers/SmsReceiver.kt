package com.sapp.yupi.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.sapp.yupi.utils.SmsUtil


class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (smsMessages.isNotEmpty()) {
                var message = ""
                for (sms in smsMessages) {
                    message += sms.messageBody
                }

                SmsUtil.handleIncomingMsg(message)
            }
        }
    }
}
