package com.sapp.yupi.data

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.RawContacts
import android.telephony.PhoneNumberUtils
import androidx.lifecycle.MutableLiveData
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.sapp.yupi.utils.PhoneUtil
import com.sapp.yupi.utils.UserInfo
import java.util.concurrent.Executor
import java.util.concurrent.Executors


/**
 *  Representation from a local contact repository
 */
class ContactRepository constructor(context: Context) {

    private val contentResolver = context.contentResolver
    private val regionCode = UserInfo.getInstance(context).regionCode

    private var contacts: MutableLiveData<List<Contact>> = MutableLiveData()
    private var executor: Executor = Executors.newSingleThreadExecutor()

//    private val sContactsObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
//
//        var lastTimeOfCall = 0L
//        var lastTimeOfUpdate = 0L
//        var thresholdTime: Long = 3000
//
//        override fun onChange(selfUpdate: Boolean) {
//            lastTimeOfCall = System.currentTimeMillis()
//            if (lastTimeOfCall - lastTimeOfUpdate > thresholdTime) {
//                contacts = getContacts()
//                lastTimeOfUpdate = System.currentTimeMillis()
//            }
//        }
//
//    }
//
//    init {
//        context.contentResolver.registerContentObserver(
//                Phone.CONTENT_URI, true, sContactsObserver)
//    }

    /**
     * Queries the caller id info with the phone number formatted to E164.
     * @return a Contact containing the caller id info corresponding to the number formatted to E164.
     */
    @SuppressLint("NewApi")
    fun getContactInfoForPhoneNumber(numberE164: String): Contact {
        val contact: Contact

        val normalizedNumber = PhoneNumberUtils.normalizeNumber(numberE164)
        val minMatch = PhoneNumberUtils.toCallerIDMinMatch(normalizedNumber)
        val numberLen = normalizedNumber.length.toString()
        val args = arrayOf(minMatch, numberE164, numberLen, normalizedNumber, numberLen)

        val cursor = contentResolver.query(Data.CONTENT_URI, PROJECTION, SELECTION, args,
                null)
        cursor.apply {
            contact = if (this != null && moveToFirst()) {
                fillContactFromCursor(this)
            } else {
                val number = PhoneUtil.toInternational(numberE164, regionCode)
                Contact(number, number)
            }

        }
        cursor?.close()

        return contact
    }


    fun getContacts(): MutableLiveData<List<Contact>> {
        executor.execute {
            val selection = ContactsContract.Data.MIMETYPE + " = ?"
            val selectionArgs = arrayOf(Phone.CONTENT_ITEM_TYPE)
            val cursor = contentResolver.query(Phone.CONTENT_URI, PROJECTION, selection,
                    selectionArgs, Phone.SORT_KEY_PRIMARY + " ASC")

            cursor?.apply {
                val list: MutableList<Contact> = mutableListOf()
                val phoneUtil = PhoneNumberUtil.getInstance()
                while (moveToNext()) {
                    try {
                        val accountType =
                                getString(getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE))
                        if (accountType == "vnd.sec.contact.phone") {
                            val phoneNumber = phoneUtil.parse(getString(getColumnIndex(Phone.NUMBER)), regionCode)

//                            val type1 = getInt(getColumnIndex(Phone.TYPE))
                            val type = phoneUtil.getNumberType(phoneNumber)
                            val isMobile = /*type2 != PhoneNumberUtil.PhoneNumberType.UNKNOWN &&
                                    type1 == Phone.TYPE_MOBILE || type1 == Phone.TYPE_WORK_MOBILE ||*/
                                    type == PhoneNumberUtil.PhoneNumberType.MOBILE ||
                                    type == PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE

                            if (isMobile &&  (regionCode == "CU" || (regionCode != "CU" &&
                                            phoneNumber.countryCode == 53))) {
                                val contact = fillContactFromCursor(this)

                                list.add(contact)
                            }
                        }
                    } catch (e: Exception) {
                        // Ignore number
                    }
                }

                contacts.postValue(list)

                close()
            }
        }

        return contacts
    }

    private fun fillContactFromCursor(cursor: Cursor) =
            cursor.let {
                val phone = it.getString(it.getColumnIndex(Phone.NUMBER))
                Contact(
                        it.getString(it.getColumnIndex(Phone.DISPLAY_NAME_PRIMARY)),
                        PhoneUtil.toInternational(phone, regionCode),
                        retrievePhoto(it.getString(it.getColumnIndex(Phone.PHOTO_THUMBNAIL_URI)))
                )
            }

    private fun retrievePhoto(photoUri: String?) = photoUri?.let { Uri.parse(it) }

    companion object {

        private val PROJECTION = arrayOf(
                RawContacts.ACCOUNT_TYPE,
                Phone.TYPE,
                Phone.NUMBER,
                Phone.DISPLAY_NAME_PRIMARY,
                Phone.PHOTO_THUMBNAIL_URI
        )

        /**
         * For a specified phone number, 2 rows were inserted into phone_lookup
         * table. One is the phone number's E164 representation, and another is
         * one's normalized format. If the phone number's normalized format in
         * the lookup table is the suffix of the given number's one, it is
         * treated as matched CallerId. E164 format number must fully equal.
         *
         * For example: Both 650-123-4567 and +1 (650) 123-4567 will match the
         * normalized number 6501234567 in the phone lookup.
         *
         * The min_match is used to narrow down the candidates for the final
         * comparison.
         */
        // query params for caller id lookup
        private const val SELECTION = (" Data._ID IN "
                + " (SELECT DISTINCT lookup.data_id "
                + " FROM "
                + " (SELECT data_id, normalized_number, length(normalized_number) as len "
                + " FROM phone_lookup "
                + " WHERE min_match = ?) AS lookup "
                + " WHERE lookup.normalized_number = ? OR"
                + " (lookup.len <= ? AND "
                + " substr(?, ? - lookup.len + 1) = lookup.normalized_number))")

        // For Singleton instantiation
        @Volatile
        private var instance: ContactRepository? = null

        fun getInstance(context: Context) =
                instance ?: synchronized(this) {
                    instance ?: ContactRepository(context).also { instance = it }
                }
    }
}