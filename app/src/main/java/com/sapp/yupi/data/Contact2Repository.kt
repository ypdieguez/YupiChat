package com.sapp.yupi.data

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.Executor
import java.util.concurrent.Executors


/**
 *  Representation from a local contact repository
 *  use this class if you want to write your own ViewModel
 *  otherwise use ContactViewModel
 */
class Contact2Repository constructor(private var context: Context) {

    val contacts: MutableLiveData<List<Contact2>> = MutableLiveData()
    private var executor: Executor
    private var defaultDataColumnName: String
    var profilesToReceive: Map<String, String>

    constructor(context: Context, executor: Executor, profilesToRetrieve: Map<String, String>, defaultDataColumnName: String = "data1") : this(context) {
        this.executor = executor
        this.profilesToReceive = profilesToRetrieve
        this.defaultDataColumnName = defaultDataColumnName
    }

    init {
        contacts.value = mutableListOf()
        executor = Executors.newSingleThreadExecutor()
        profilesToReceive = DefaultProfiles.profiles
        defaultDataColumnName = "data1"
    }

    fun getContacts() {
        val cursor = context.contentResolver.query(Phone.CONTENT_URI,
                null, null, null, Phone.SORT_KEY_PRIMARY + " ASC")

        cursor?.apply {
            val list: MutableList<Contact2> = mutableListOf()
            while (moveToNext()) {
                val contact = Contact2()
                contact.id = getLong(getColumnIndex(Phone.CONTACT_ID))
                contact.name = getString(getColumnIndex(Phone.DISPLAY_NAME_PRIMARY))
                contact.number = getString(getColumnIndex(Phone.NUMBER))
                contact.photoUri = retrievePhoto(getString(getColumnIndex(Phone.PHOTO_URI)))
                contact.thumbnailUri = retrievePhoto(getString(getColumnIndex(Phone.PHOTO_THUMBNAIL_URI)))
                list.add(contact)
            }

            contacts.postValue(list)

            close()
        }
    }

    // query the contact
    fun getContacts(uri: Uri, projection: Array<String>?,
                    selection: String?, selectionArgs: Array<String>?, sortOrder: String) {
        executor.execute {
            val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)

            cursor?.let {
                val list: MutableList<Contact2> = mutableListOf()
                while (it.moveToNext()) {
                    val contact = Contact2()
                    contact.id = it.getLong(it.getColumnIndex(ContactsContract.Contacts._ID))
                    contact.name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                    contact.photoUri = retrievePhoto(it.getString(
                            it.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)))
                    contact.thumbnailUri = retrievePhoto(it.getString(
                            it.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)))

                    list.add(contact)
                }

                // fast loading for recyclerViews
                contacts.postValue(list)

                for (contact in list) {
//                    contact.profiles = retrieveProfiles(contact.id.toString())
//                    contact.number = retrieveNumbers(contact.id.toString())
                }

                contacts.postValue(list)
            }

            cursor?.close()
        }
    }

    private fun retrievePhoto(photoUri: String?) = photoUri?.let { Uri.parse(it) }


    private fun retrieveNumbers(contactId: String): MutableList<String> {
        val phoneCursor = context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                arrayOf(contactId),
                null)

        val numbers = mutableListOf<String>()

        while (phoneCursor != null && phoneCursor.moveToNext()) {
            val number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            numbers.add(number)
        }

        phoneCursor?.close()
        return numbers
    }

    private fun retrieveProfiles(contactId: String): MutableMap<String, String> {
        val profiles = mutableMapOf<String, String>()
        val projection = arrayOf(ContactsContract.Data.MIMETYPE) + profilesToReceive.values.distinct()
        val selection = ContactsContract.Data.CONTACT_ID + " = ? AND " +
                ContactsContract.Data.MIMETYPE + " IN ( " + placeholder(profilesToReceive.size) + ") "
        val selectionArgs = arrayOf(contactId) + profilesToReceive.keys
        val cursor = context.contentResolver.query(ContactsContract.Data.CONTENT_URI,
                projection, selection, selectionArgs, null)

        while (cursor != null && cursor.moveToNext()) {
            val type = cursor.getString(0)
            val index = cursor.getColumnIndex(if (profilesToReceive.containsKey(type))
                profilesToReceive[type]!! else defaultDataColumnName)

            if (index == -1)
                throw Exception("Couldn't find $type column in CONTENT_URI.")

            val data = cursor.getString(index)

            profiles[type] = data
        }

        cursor?.close()
        return profiles
    }

    private fun placeholder(count: Int): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("?")
        repeat(count - 1) {
            stringBuilder.append(",?")
        }
        return stringBuilder.toString()
    }
}

/**
 *  contains some default profiles the contact might have
 *  key (first) = MimeType
 *  value (second) = columnName
 */
class DefaultProfiles {
    companion object {
        /**
         *  WhatsApp, Signal an Telegram are using Data1 to reach the user directly
         *  Data2 is representing the apps name and data3 is some sort of action text so
         *  the user knows what action can be done with data1 (for example Message +49 XXX XXXX)
         *  data4 was only used by telegram and the information was the same as in data1 and may represent
         *  the primary user id (if the user has more then one number)
         */
        val WHATSAPP = Pair(
                "vnd.android.cursor.item/vnd.com.whatsapp.profile",
                "data1"
        )
        val TELEGRAM = Pair(
                "vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile",
                "data1"
        )
        val SIGNAL = Pair(
                "vnd.android.cursor.item/vnd.org.thoughtcrime.securesms.contact",
                "data1"
        )
        val MAIL = Pair(
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Email.ADDRESS
        )
        val NUMBER = Pair(
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val profiles = mapOf(WHATSAPP, TELEGRAM, SIGNAL, MAIL, NUMBER)
    }
}