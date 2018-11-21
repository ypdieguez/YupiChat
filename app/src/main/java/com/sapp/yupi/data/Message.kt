package com.sapp.yupi.data

import androidx.annotation.NonNull
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.util.*

@Entity(
        tableName = "messages",
        foreignKeys = [ForeignKey(entity = Contact::class, parentColumns = ["id"],
                childColumns = ["contact_id"], onDelete = CASCADE, onUpdate = CASCADE)],
        indices = [Index("text")]
)
class Message(
        @NonNull
        @ColumnInfo(name = "contact_id")
        var contactId: Long,
        @NonNull
        @ColumnInfo(name = "msg_id")
        var msgId: Long,
        @NonNull
        var type: Boolean,
        @NonNull
        var status: Byte,
        @NonNull
        var date : Long = Calendar.getInstance().timeInMillis,
        @NonNull
        var text: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}