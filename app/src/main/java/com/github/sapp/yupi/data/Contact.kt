package com.github.sapp.yupi.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.NonNull

@Entity(tableName = "contacts")
data class Contact(
        @NonNull
        val name: String,
        @NonNull
        val phone: String,
        @ColumnInfo(name = "photo_url")
        val photoUrl: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}