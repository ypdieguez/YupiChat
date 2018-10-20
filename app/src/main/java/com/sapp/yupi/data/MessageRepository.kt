package com.sapp.yupi.data

import com.sapp.yupi.runOnIoThread

class MessageRepository private constructor(private val messageDao: MessageDao) {

    fun insert(message: Message) {
        runOnIoThread { messageDao.insert(message) }
    }

    fun delete(message: Message) {
        runOnIoThread { messageDao.delete(message) }
    }

    fun getMessagesForContact(contactId: Int) = messageDao.getMessagesForContact(contactId)

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: MessageRepository? = null

        fun getInstance(messageDao: MessageDao) =
                instance ?: synchronized(this) {
                    instance ?: MessageRepository(messageDao).also { instance = it }
                }
    }
}