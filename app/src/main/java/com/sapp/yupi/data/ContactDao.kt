package com.sapp.yupi.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * The Data Access Object for the [Contact] class.
 */
@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contact: Contact): Long

    @Update
    fun update(contact: Contact)

    @Delete
    fun delete(contact: Contact)

    @Query("DELETE FROM contacts")
    fun deleteAll()

    @Query("SELECT * FROM contacts")
    fun getLiveContacts(): LiveData<List<Contact>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getLiveContact(id: Long): LiveData<Contact>

    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getContact(id: Long): Contact

    @Query("SELECT * FROM contacts WHERE phone = :phone")
    fun getContactByPhone(phone: String): Contact?
}