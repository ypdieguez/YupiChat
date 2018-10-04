package com.github.sapp.yupi.data

import com.github.sapp.yupi.runOnIoThread

class ContactRepository private constructor(private val contactDao: ContactDao) {

    fun getContacts() = contactDao.getContacts()

    fun getContact(id: Int) = contactDao.getContact(id)

    fun insert(contact: Contact) {
        runOnIoThread { contactDao.insert(contact) }
    }

    fun update(contact: Contact) {
        runOnIoThread { contactDao.update(contact) }
    }

    fun delete(contact: Contact) {
        runOnIoThread { contactDao.delete(contact) }
    }

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