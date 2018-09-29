package com.github.sapp.yupi.data

class ContactRepository private constructor(private val contactDao: ContactDao) {

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