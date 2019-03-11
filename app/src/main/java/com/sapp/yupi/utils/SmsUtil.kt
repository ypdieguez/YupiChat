package com.sapp.yupi.utils

import android.util.Patterns
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.sapp.yupi.CONTACT_PHONE
import com.sapp.yupi.MESSAGE
import com.sapp.yupi.workers.*

class SmsUtil {
    companion object {

        private const val DELIMITER = "\n\n"

        fun handleOutgoingMsg(phone: String, txt: String, id: Long = -1) {
            val data = Data.Builder()
                    .putLong(MESSAGE_ID, id)
                    .putString(CONTACT_PHONE, phone)
                    .putString(MESSAGE, txt)
                    .build()

            val sendMsgWorker = OneTimeWorkRequest.Builder(OutgoingMsgWorker::class.java)
                    .setInputData(data)
                    .build()

            val workManager = WorkManager.getInstance()
            workManager.enqueue(sendMsgWorker)
        }

        fun handleIncomingMsg(date:Long, body: String) {

            val phone = parsePhone(body)
            if (Patterns.PHONE.matcher(phone).matches()) {
                val msg = parseMsg(body)

                // Do the work
                val data = Data.Builder()
                        .putLong(KEY_DATE, date)
                        .putString(KEY_PHONE, phone)
                        .putString(KEY_BODY, msg)
                        .build()

                val worker = OneTimeWorkRequest.Builder(IncomingMsgWorker::class.java)
                        .setInputData(data)
                        .build()

                WorkManager.getInstance().enqueue(worker)
            }
        }

        fun createMsg(content:String, phone: String) =  "$content$DELIMITER$phone"

        private fun parsePhone(body: String) = body.substringAfterLast(DELIMITER)

        private fun parseMsg(body: String) = body.substringBeforeLast(DELIMITER)
    }
}