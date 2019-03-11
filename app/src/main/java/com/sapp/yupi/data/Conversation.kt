package com.sapp.yupi.data

import androidx.room.*

@Entity(
        tableName = "conversations",
        indices = [Index("last_message_date")]
)
data class Conversation(
        var error: Boolean = false,
        var read: Boolean = true,
        @ColumnInfo(name = "message_count")
        var messageCount: Int = 0,
        @ColumnInfo(name = "last_message_date")
        var lastMessageDate: Long = 0,
        @PrimaryKey
        var phone: String,
        var snippet: String
) {
    @Ignore
    lateinit var contact:Contact
}