package com.sapp.yupi.data

import com.sapp.yupi.runOnIoThread

class ContactRepository private constructor(private val contactDao: ContactDao) {

    fun insert(contact: Contact) {
        runOnIoThread { contactDao.insert(contact) }
    }

    fun update(contact: Contact) {
        runOnIoThread { contactDao.update(contact) }
    }

    fun delete(contact: Contact) {
        runOnIoThread { contactDao.delete(contact) }
    }

    fun getContacts() = contactDao.getContacts()

    fun getContact(id: Int) = contactDao.getContact(id)

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: ContactRepository? = null

        fun getInstance(contactDao: ContactDao) =
                instance ?: synchronized(this) {
                    instance ?: ContactRepository(contactDao).also { instance = it }
                }
    }
}