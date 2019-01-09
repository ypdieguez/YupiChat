package com.sapp.yupi.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * The Data Access Object for the [Conversation] class.
 */
@Dao
interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(conversation: Conversation): Long

    @Update
    fun update(conversation: Conversation)

    @Delete
    fun delete(conversation: Conversation)

    @Query("DELETE FROM conversations")
    fun deleteAll()

    @Query("SELECT * FROM conversations ORDER BY last_message_date DESC")
    fun getConversations(): LiveData<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    fun getConversation(id: Long): LiveData<Conversation>

    @Query("SELECT * FROM contacts WHERE phone = :phone")
    fun getConversationByPhone(phone: String): Contact?
}