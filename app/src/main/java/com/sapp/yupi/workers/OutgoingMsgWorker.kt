package com.sapp.yupi.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.material.snackbar.Snackbar
import com.sapp.yupi.*
import com.sapp.yupi.data.AppDatabase
import com.sapp.yupi.data.Message

class OutgoingMsgWorker(context: Context, params: WorkerParameters)
    : Worker(context, params) {

    override fun doWork(): Result {

        // Get data
        val contactId = inputData.getLong(CONTACT_ID, 0)
        val txt = inputData.getString(MESSAGE)

        // Get MessageDao
        val db = AppDatabase.getInstance(applicationContext)

        // Insert msg into db
        val msg = Message(contactId, 0,TYPE_OUTGOING, STATUS_SENDING, text = txt!!)
        val id = db.messageDao().insert(msg)

        // Send msg
        val phone  = db.contactDao().getContact(contactId).phone
        val status = Mail.send(applicationContext, "gtom20180828@gmail.com", phone, txt)

        // Update msg into db
        msg.id = id
        msg.status = status
        db.messageDao().update(msg)

        return Result.Success.success()
    }
}
