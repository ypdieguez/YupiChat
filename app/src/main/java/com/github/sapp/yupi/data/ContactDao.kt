package com.github.sapp.yupi.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * The Data Access Object for the [Contact] class.
 */
@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contact: Contact)

    @Update
    fun update(contact: Contact)

    @Delete
    fun delete(contact: Contact)

    @Query("SELECT * FROM contacts")
    fun getContacts(): LiveData<List<Contact>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getContact(id: Int): LiveData<Contact>

    @Query("DELETE FROM contacts")
    fun deleteAll()
}