package com.sapp.yupi.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
        tableName = "contacts",
        indices = [Index("name"), Index(value = ["phone"], unique = true)]
)
data class Contact(
        @NonNull
        var name: String,
        @NonNull
        var phone: String,
        @ColumnInfo(name = "photo_url")
        var photoUrl: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}