package com.sapp.yupi.workers

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.sapp.yupi.*
import com.sapp.yupi.data.*
import com.sapp.yupi.utils.PhoneUtil
import com.sapp.yupi.UserPref
import com.sapp.yupi.receivers.NotificationReceiver
import java.util.*

/**
 *  Body of sms message
 */
const val KEY_BODY = "body"
/**
 * Phone sender
 */
const val KEY_PHONE = "phone"

class IncomingMsgWorker(context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {

    override fun doWork(): Result {
        try {
            val context = applicationContext

            // Get DB
            val db = AppDatabase.getInstance(context)

            // Fetch data
            val phone = inputData.getString(KEY_PHONE)!!
            val text = inputData.getString(KEY_BODY)!!

            // Utils
            val user = UserPref.getInstance(context)
            val phoneUtil = PhoneNumberUtil.getInstance()

            if (phoneUtil.isNumberMatch(user.phone, phone) != PhoneNumberUtil.MatchType.EXACT_MATCH) {
                // Get date
                val date = Calendar.getInstance().timeInMillis
                // Insert or update conversation
                val conv = db.conversationDao().getConversationByPhone(phone)
                if (conv == null) {
                    db.conversationDao().insert(Conversation(
                            read = false,
                            lastMessageDate = date,
                            phone = phone,
                            snippet = text
                    ))
                } else {
                    conv.read = false
                    conv.lastMessageDate = date
                    conv.snippet = text
                    db.conversationDao().update(conv)
                }

                // Insert msg into db
                val msg = Message(TYPE_INCOMING, STATUS_SUCCESS, date, text, phone)
                db.messageDao().insert(msg)

                // Notify
                val contact = if (ContextCompat.checkSelfPermission(context,
                                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    ContactRepository.getInstance(context).getContactInfoForPhoneNumber(phone)
                } else {
                    val number = PhoneUtil.toInternational(phone, user.regionCode)
                    Contact(number, number)
                }

                sendNotification(Notification(contact, text), phone)
            } else {
                // This process will trigger once
                if (!user.phoneValidated) {
                    // This only will happen when phone is in validating process
                    user.phoneValidated = true

                    // Send to notify the phone validation
                    sendNotification(Notification(
                            Contact(context.getString(R.string.app_name), phone),
                            context.getString(R.string.subscription)
                    ), phone)
                }
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun sendNotification(notification: Notification, phone: String) {
        // Register NotificationReceiver
//        val filter = IntentFilter(BROADCAST_NOTIFICATION)
//        filter.priority = 0
//        applicationContext.registerReceiver(NotificationReceiver(), filter)

        // Create broadcast
        val extras = Bundle()
        extras.putParcelable(NOTIFICATION, notification)
        extras.putString(PHONE_NOTIFICATION, phone)

        val intent = Intent()
        intent.putExtras(extras)
        intent.action = BROADCAST_NOTIFICATION
        intent.setPackage(applicationContext.packageName)

        // Send broadcast
        applicationContext.sendOrderedBroadcast(intent, null)
    }
}