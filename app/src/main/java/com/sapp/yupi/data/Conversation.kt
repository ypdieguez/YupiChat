package com.sapp.yupi.data

import androidx.annotation.NonNull
import androidx.room.*

@Entity(
        tableName = "conversations",
        indices = [Index("last_message_date"), Index(value = ["phone"], unique = true)]
)
data class Conversation(
        var error: Boolean,
        var read: Boolean,
        @ColumnInfo(name = "message_count")
        var messageCount: Int,
        @ColumnInfo(name = "last_message_date")
        var lastMessageDate: Long,
        @NonNull
        @ColumnInfo()
        var phone: String,
        var snippet: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @Ignore
    var contact:Contact2?  = null
}