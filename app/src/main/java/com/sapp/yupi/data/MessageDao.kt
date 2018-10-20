package com.sapp.yupi.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(message: Message)

    @Delete
    fun delete(message: Message)

    @Query("SELECT * FROM messages WHERE contact_id = :contactId")
    fun getMessagesForContact(contactId: Int): LiveData<List<Message>>

    @Query("DELETE FROM messages")
    fun deleteAll()
}