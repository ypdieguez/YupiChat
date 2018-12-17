package com.sapp.yupi.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sapp.yupi.STATUS_SUCCESS
import com.sapp.yupi.TYPE_INCOMING
import com.sapp.yupi.data.AppDatabase
import com.sapp.yupi.data.Contact
import com.sapp.yupi.data.Message
import java.util.*
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.content.SharedPreferences
import com.sapp.yupi.IncomingMsgNotification

const val KEY_MSG_ID = "msg_id"
const val KEY_DATE = "date"
const val KEY_BODY = "body"

class IncomingMsgWorker(context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Get DB
        val db = AppDatabase.getInstance(applicationContext)
        // Get Data
        val msgId = inputData.getLong(KEY_MSG_ID, 0)

        var msg = db.messageDao().getMessageWithMsgId(msgId)

        if (msg == null) {
            // Fetch more data
            val date = inputData.getLong(KEY_DATE, Calendar.getInstance().timeInMillis)
            val body = inputData.getString(KEY_BODY)

            // Parse body
            val text = body!!.substringBeforeLast("\n\nEnviado por:")

            val info = body.substringAfterLast("\n\nEnviado por:\n")
                    .substringBefore("\nDesde:")

            val nick = info.substringAfter("Nick: ").substringBefore("\n")
            val phone = info.substringAfter("Cell: ").substringBefore("\n")

            // Get ContactDao
            val contact = db.contactDao().getContactByPhone(phone)
            // Check if contact exist, if not insert him.
            val id = contact?.id ?: db.contactDao().insert(Contact(nick, phone))
            // Insert msg into db
            msg = Message(id, msgId, TYPE_INCOMING, STATUS_SUCCESS, date, text)
            db.messageDao().insert(msg)

            // Notify
            IncomingMsgNotification.notify(applicationContext, "Tienes un mensaje nuevo.", 1)
        }

        return Result.Success.success()
    }
}