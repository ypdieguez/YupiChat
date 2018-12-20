package com.sapp.yupi.observers

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import android.provider.Telephony.Mms
import android.telephony.PhoneNumberUtils
import com.sapp.yupi.mmslib.pdu.EncodedStringValue
import com.sapp.yupi.mmslib.util.AddressUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class MmsObserver(private val contentResolver: ContentResolver) : MessageObserver() {

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)

        // Check MMS is received
        val cursor = getLastMms()
        cursor?.apply {
            if (moveToFirst()) {
                val messageBox = getInt(getColumnIndex(Telephony.Mms.MESSAGE_BOX))
                if (messageBox == Telephony.Mms.MESSAGE_BOX_INBOX) {
                    // It's received MMS
                    receiveMms(cursor)
                }
            }

            close()
        }
    }

    private fun getLastMms(): Cursor? {
        val projection = arrayOf(
                Telephony.Mms._ID,
                Telephony.Mms.MESSAGE_BOX,
                Telephony.Mms.DATE
        )
        val sortOrder = Telephony.Mms.DEFAULT_SORT_ORDER + " LIMIT 1"

        return contentResolver.query(Telephony.Mms.CONTENT_URI, projection, null,
                null, sortOrder)
    }

    private fun receiveMms(mmsCursor: Cursor) {
        mmsCursor.apply {
            val id = getLong(getColumnIndex(Mms._ID))

            if (PhoneNumberUtils.compare(AddressUtils.getFrom(contentResolver, id.toString()),
                            "+5352362191")) {
                val date = getLong(getColumnIndex(Mms.DATE))

                val uri = Mms.CONTENT_URI.buildUpon().appendPath("part").build()
                val projection = arrayOf(Mms.Part._ID, Mms.Part.CONTENT_TYPE, Mms.Part._DATA,
                        Mms.Part.TEXT, Mms.Part.CHARSET)
                val selection = "${Mms.Part.MSG_ID}=$id"

                val partCursor = contentResolver.query(uri, projection, selection, null,
                        null)

                partCursor?.apply {
                    if (moveToFirst()) {
                        var body = ""
                        do {
                            val type = getString(getColumnIndex(Mms.Part.CONTENT_TYPE))
                            if ("text/plain" == type) {
                                val data = getString(getColumnIndex(Mms.Part._DATA))
                                body = if (data != null) {
                                    val partId = getString(getColumnIndex(Mms._ID))
                                    getMmsText(partId)
                                } else {
                                    val text = getString(getColumnIndex(Mms.Part.TEXT))
                                    val charset = getInt(getColumnIndex(Mms.Part.CHARSET))
                                    EncodedStringValue(charset, text).string
                                }
                            }
                        } while (partCursor.moveToNext())

                        handleMsg(id, date, body)
                    }

                    close()
                }
            }
        }
    }

    private fun getMmsText(id: String): String {
        val partURI = Uri.parse("content://mms/part/$id")
        var `is`: InputStream? = null
        val sb = StringBuilder()
        try {
            `is` = contentResolver.openInputStream(partURI)
            if (`is` != null) {
                val isr = InputStreamReader(`is`, "UTF-8")
                val reader = BufferedReader(isr)
                var temp: String? = reader.readLine()
                while (temp != null) {
                    sb.append(temp)
                    temp = reader.readLine()
                }
            }
        } catch (e: IOException) {
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                }

            }
        }
        return sb.toString()
    }
}