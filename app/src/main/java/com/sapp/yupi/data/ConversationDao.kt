package com.sapp.yupi.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * The Data Access Object for the [Conversation] class.
 */
@Dao
interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(conversation: Conversation)

    @Update
    fun update(conversation: Conversation)

    @Delete
    fun delete(conversation: Conversation)

    @Query("DELETE FROM conversations")
    fun deleteAll()

    @Query("SELECT * FROM conversations ORDER BY last_message_date DESC")
    fun getConversations(): LiveData<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE phone = :phone")
    fun getConversation(phone: String): LiveData<Conversation>

    @Query("SELECT * FROM conversations WHERE phone = :phone")
    fun getConversationByPhone(phone: String): Conversation?

    @Query("UPDATE conversations SET read = 1 WHERE phone = :phone")
    fun markConversationAsRead(phone: String)
}