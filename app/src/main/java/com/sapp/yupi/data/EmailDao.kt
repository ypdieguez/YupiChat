package com.sapp.yupi.data

import androidx.room.*

@Dao
interface EmailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(email: Email): Long

    @Delete
    fun delete(email: Email)

    @Query("SELECT * FROM emails")
    fun getAllEmails(): List<Email>
}