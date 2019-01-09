package com.sapp.yupi.data

import com.sapp.yupi.runOnIoThread
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class ConversationRepository private constructor(private val conversationDao: ConversationDao) {

    suspend fun insert(conversation: Conversation) {
        withContext(IO) { conversationDao.insert(conversation) }
    }

    suspend fun update(conversation: Conversation) {
        withContext(IO) { conversationDao.update(conversation) }
    }

    suspend fun delete(conversation: Conversation) {
        withContext(IO) { conversationDao.delete(conversation) }
    }

    fun getConversations() = conversationDao.getConversations()

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: ConversationRepository? = null

        fun getInstance(conversationDao: ConversationDao) =
                instance ?: synchronized(this) {
                    instance ?: ConversationRepository(conversationDao).also { instance = it }
                }
    }
}