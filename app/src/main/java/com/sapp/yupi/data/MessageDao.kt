package com.sapp.yupi.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(message: Message): Long

    @Update
    fun update(msg: Message)

    @Delete
    fun delete(message: Message)

    @Query("DELETE FROM messages")
    fun deleteAll()

    @Query("SELECT * FROM messages WHERE contact_id = :contactId")
    fun getMessagesForContact(contactId: Long): LiveData<List<Message>>

    @Query("SELECT * FROM messages WHERE msg_id = :msgId")
    fun getMessageWithMsgId(msgId: Long): Message?
}