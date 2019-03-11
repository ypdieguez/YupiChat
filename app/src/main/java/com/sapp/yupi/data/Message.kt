package com.sapp.yupi.data

import androidx.annotation.NonNull
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.util.*

@Entity(
        tableName = "messages",
        foreignKeys = [ForeignKey(entity = Conversation::class, parentColumns = ["phone"],
                childColumns = ["phone"], onDelete = CASCADE, onUpdate = CASCADE)],
        indices = [Index(value = ["phone"])]
)
data class Message(
        @NonNull
        var type: Boolean,
        @NonNull
        var status: Byte,
        @NonNull
        var date : Long = Calendar.getInstance().timeInMillis,
        @NonNull
        var text: String,
        @NonNull
        var phone: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}