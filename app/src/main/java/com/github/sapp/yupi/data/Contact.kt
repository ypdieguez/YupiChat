package com.github.sapp.yupi.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.NonNull
import androidx.annotation.Nullable

@Entity(tableName = "contacts")
data class Contact(
        @NonNull
        var name: String,
        @NonNull
        var phone: String,
        @ColumnInfo(name = "photo_url")
        var photoUrl: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}