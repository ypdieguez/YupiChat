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

    @Query("SELECT * FROM messages WHERE phone = :phone ORDER BY date")
    fun getMessagesForConversation(phone: String): LiveData<List<Message>>

    @Query("SELECT * FROM messages WHERE id = :id")
    fun getMessage(id: Long): Message
//
//    @Query("SELECT * FROM messages WHERE sms_id = :smsId")
//    fun getMessageWithSmsId(smsId: Long): Message?
}