package com.sapp.yupi.observers

import android.content.ContentResolver
import android.database.Cursor
import android.provider.Telephony
import android.telephony.PhoneNumberUtils

class SmsObserver(private val contentResolver: ContentResolver) : MessageObserver() {

    var lastSmsIntercepted = 0L

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)

        // Check if SMS is received
        val cursor = getLastSms()
        cursor?.apply {
            if (moveToFirst()) {
                val id = getLong(getColumnIndex(Telephony.Sms._ID))
                if (id != lastSmsIntercepted) {
                    val type = getInt(getColumnIndex(Telephony.Sms.TYPE))
                    if (type == Telephony.Sms.MESSAGE_TYPE_INBOX) {
                        // It's received SMS
                        receiveSms(this)
                    }

                    lastSmsIntercepted = id
                }
            }

            close()
        }

    }

    private fun getLastSms(): Cursor? {
        val projection = arrayOf(
                Telephony.Sms._ID,
                Telephony.Sms.TYPE,
                Telephony.Sms.DATE,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY
        )
        val sortOrder = Telephony.Sms.DEFAULT_SORT_ORDER + " LIMIT 1"

        return contentResolver.query(Telephony.Sms.CONTENT_URI, projection, null,
                null, sortOrder)
    }

    private fun receiveSms(cursor: Cursor) {
        cursor.apply {
            val address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS))
            if (PhoneNumberUtils.compare(address, "+5352871805")) {
                // Get data
                val id = getLong(getColumnIndex(Telephony.Sms._ID))
                val date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE))
                val body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY))

                // Do the work
                insertMsg(id, date, body)
            }
        }
    }
}