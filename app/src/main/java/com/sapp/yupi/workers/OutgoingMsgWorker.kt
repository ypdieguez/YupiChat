package com.sapp.yupi.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sapp.yupi.*
import com.sapp.yupi.data.AppDatabase
import com.sapp.yupi.data.Conversation
import com.sapp.yupi.data.Message
import com.sapp.yupi.utils.Email
import java.util.*

const val MESSAGE_ID = "message_id"

class OutgoingMsgWorker(context: Context, params: WorkerParameters)
    : Worker(context, params) {

    override fun doWork(): Result {
        try {
            // Get data
            var id = inputData.getLong(MESSAGE_ID, -1)
            val phone = inputData.getString(CONTACT_PHONE)!!
            val txt = inputData.getString(MESSAGE)!!

            // Get date
            val date = Calendar.getInstance().timeInMillis

            // Get MessageDao
            val db = AppDatabase.getInstance(applicationContext)

            val conv = db.conversationDao().getConversationByPhone(phone)
            if (conv == null) {
                db.conversationDao().insert(Conversation(
                        lastMessageDate = date,
                        phone = phone,
                        snippet = txt
                ))
            } else {
                db.conversationDao().update(Conversation(
                        lastMessageDate = date,
                        phone = phone,
                        snippet = txt
                ))
            }

            val msg: Message
            if (id == -1L) {
                // Insert msg into db
                msg = Message(TYPE_OUTGOING, STATUS_SENDING, date, txt, phone)
                id = db.messageDao().insert(msg)
                msg.id = id
            } else {
                // Update msg into db
                msg = db.messageDao().getMessage(id)
                msg.status = STATUS_SENDING
                msg.date = date
                db.messageDao().update(msg)
            }

            // Send msg
            val status = Email.getInstance(applicationContext)
                    .send(BuildConfig.RECIPIENT_EMAIL, phone, txt)

            // Update status msg
            msg.status = status
            db.messageDao().update(msg)

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}
